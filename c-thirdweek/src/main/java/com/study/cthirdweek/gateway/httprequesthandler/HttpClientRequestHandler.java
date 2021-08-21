package com.study.cthirdweek.gateway.httprequesthandler;

import com.study.cthirdweek.gateway.filter.request.RequestFilterChain;
import com.study.cthirdweek.gateway.filter.response.ResponseFilterChain;
import com.study.cthirdweek.gateway.utils.HttpClientUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.multipart.DefaultHttpDataFactory;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import io.netty.handler.codec.http.multipart.MemoryAttribute;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.ProtocolVersion;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
public class HttpClientRequestHandler implements RequestHandler {

    private static volatile HttpClientRequestHandler httpClientRequestHandler = null;

    private HttpClientRequestHandler() {
    }

    public static HttpClientRequestHandler getInstance() {
        if (httpClientRequestHandler == null) {
            synchronized (HttpClientRequestHandler.class) {
                if (httpClientRequestHandler == null) {
                    httpClientRequestHandler = new HttpClientRequestHandler();
                }
            }
        }
        return httpClientRequestHandler;
    }

    @Override
    public void handler(FullHttpRequest request, ChannelHandlerContext ctx) {
        //获取实际访问的地址
        String serverUrl = getRealUrl(request);

        //请求前过滤器处理
        RequestFilterChain.beforeHandler(request);

        //执行HTTP请求
        org.apache.http.HttpResponse httpResponse = executor(request, serverUrl);
        FullHttpResponse response = convertResponse(ctx, httpResponse);

        //请求后过滤器处理
        ResponseFilterChain.afterHandler(response);

        //请求结果返回
        ctx.writeAndFlush(response);
    }

    /**
     * 通过HttpClient执行HTTP请求
     */
    private org.apache.http.HttpResponse executor(FullHttpRequest fullHttpRequest, String serverUrl) {
        //请求头获取
        Map<String, String> headers = headerConvert(fullHttpRequest);
        //当前请求方法
        String method = fullHttpRequest.method().name();
//        MyThreadPool.getExecutePool().execute(() -> {
        org.apache.http.HttpResponse httpResponse;
        try {
            switch (method) {
                case HttpGet.METHOD_NAME:
                    httpResponse = HttpClientUtil.get(serverUrl, null, headers);
                    break;
                case HttpPost.METHOD_NAME:
                    String contentType = fullHttpRequest.headers().get("Content-Type");
                    if (Objects.equals(contentType, "application/json")) {
                        httpResponse = HttpClientUtil.jsonPost(
                                serverUrl,
                                fullHttpRequest.content().toString(CharsetUtil.UTF_8),
                                headers
                        );
                    } else {
                        Map<String, Object> params = getRequestParams(fullHttpRequest);
                        httpResponse = HttpClientUtil.post(serverUrl, params, headers);
                    }
                    break;
                default:
                    //TODO PUT DELETE 等其他方法处理...
                    log.error("暂不支持该请求类型：{}", method);
                    httpResponse = new BasicHttpResponse(new BasicStatusLine(
                            new ProtocolVersion("HTTP", 1, 1),
                            HttpResponseStatus.METHOD_NOT_ALLOWED.code(),
                            HttpResponseStatus.METHOD_NOT_ALLOWED.reasonPhrase()
                    ));
            }
        } catch (Exception e) {
            httpResponse = new BasicHttpResponse(new BasicStatusLine(
                    new ProtocolVersion("HTTP", 1, 1),
                    HttpResponseStatus.INTERNAL_SERVER_ERROR.code(),
                    e.getMessage() == null ? "gateway server error" : e.getMessage()
            ));
        }
        return httpResponse;
//        });
    }

    /**
     * org.apache.http.HttpResponse httpResponse转换成io.netty.handler.codec.http.FullHttpResponse
     */
    private FullHttpResponse convertResponse(ChannelHandlerContext ctx, org.apache.http.HttpResponse httpResponse) {
        ByteBuf buffer = ctx.alloc().buffer();
        String responseData;
        if (httpResponse.getStatusLine().getStatusCode() == 500) {
            responseData = httpResponse.getStatusLine().getReasonPhrase();
        } else {
            try {
                responseData = EntityUtils.toString(httpResponse.getEntity());
            } catch (IOException e) {
                responseData = "ERROR:" + e.getMessage();
            }
        }

        buffer.writeBytes(responseData.getBytes(StandardCharsets.UTF_8));
        return new DefaultFullHttpResponse(
                HttpVersion.valueOf(httpResponse.getStatusLine().getProtocolVersion().toString()),
                HttpResponseStatus.valueOf(httpResponse.getStatusLine().getStatusCode()),
                buffer
        );
    }

    /**
     * 解析表单传参
     */
    private Map<String, Object> getRequestParams(FullHttpRequest request) {
        HttpPostRequestDecoder decoder = new HttpPostRequestDecoder(new DefaultHttpDataFactory(false), request);
        List<InterfaceHttpData> httpPostData = decoder.getBodyHttpDatas();
        Map<String, Object> params = new HashMap<>();
        for (InterfaceHttpData data : httpPostData) {
            if (data.getHttpDataType() == InterfaceHttpData.HttpDataType.Attribute) {
                MemoryAttribute attribute = (MemoryAttribute) data;
                params.put(attribute.getName(), attribute.getValue());
            }
        }
        return params;
    }

    /**
     * 请求头解析
     */
    private Map<String, String> headerConvert(FullHttpRequest request) {
        Map<String, String> headerMap = new HashMap<>();
        HttpHeaders headers = request.headers();
        List<Map.Entry<String, String>> entries = headers.entries();
        for (Map.Entry<String, String> entry : entries) {
            headerMap.put(entry.getKey(), entry.getValue());
        }
        return headerMap;
    }

}
