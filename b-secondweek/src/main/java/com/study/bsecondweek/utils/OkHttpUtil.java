package com.study.bsecondweek.utils;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 基于OKHttp3.14.4实现接口调用
 */
@Slf4j
public class OkHttpUtil {

    //请求连接超时时间
    private static final int connectTimeout = 5000;
    //数据传输超时时间 请求已经执行 但是耗时较久的请求 这里可以适当调大
    private static final int transferTimeout = 10000;

    private static OkHttpClient okHttpClient = initOkHttpClient();

    /**
     * 构建 OkHttpClient
     */
    private static OkHttpClient initOkHttpClient() {
        if (okHttpClient == null) {
            synchronized (OkHttpUtil.class) {
                if (okHttpClient == null) {
                    okHttpClient = new OkHttpClient().newBuilder()
                            .connectTimeout(connectTimeout, TimeUnit.MILLISECONDS)
                            .readTimeout(transferTimeout, TimeUnit.MILLISECONDS)
                            .build();
                }
            }
        }
        return okHttpClient;
    }

    public static String get(String url) {
        return get(url, null, null);
    }

    public static String get(String url, Map<String, Object> params) {
        return get(url, params, null);
    }

    public static String get(String url, Map<String, Object> params, Map<String, String> headers) {
        //参数构建
        StringBuffer sb = new StringBuffer(url);
        if (params != null && !params.isEmpty()) {
            sb.append("?");
            Iterator<Map.Entry<String, Object>> iterator = params.entrySet().iterator();
            while (true) {
                Map.Entry<String, Object> next = iterator.next();
                sb.append(next.getKey()).append("=").append(next.getValue());
                if (iterator.hasNext()) {
                    sb.append("&");
                } else {
                    break;
                }
            }
        }
        Request.Builder requestBuilder = new Request.Builder();
        if (headers != null && !headers.isEmpty()) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                requestBuilder.addHeader(entry.getKey(), entry.getValue());
            }
        }
        Request request = requestBuilder.url(sb.toString()).build();
        //执行HTTP请求
        try (Response response = okHttpClient.newCall(request).execute()) {
            ResponseBody body = response.body();
            if (response.isSuccessful()) {
                return body == null ? null : body.string();
            } else {
                log.error("请求失败 code={}, body={}", response.code(), body == null ? "" : body.string());
                return null;
            }
        } catch (IOException e) {
            log.error("请求执行失败 e={}", e);
            throw new RuntimeException(e);
        }
    }

    public static String post(String url, Map<String, Object> params) {
        return post(url, params, null, false);
    }

    public static String post(String url, Map<String, Object> params, Map<String, String> headers) {
        return post(url, params, headers, false);
    }

    public static String jsonPost(String url, Map<String, Object> params) {
        return post(url, params, null, true);
    }

    public static String jsonPost(String url, Map<String, Object> params, Map<String, String> headers) {
        return post(url, params, headers, true);
    }

    /**
     * post请求
     *
     * @param url        请求URL
     * @param params     请求参数 key和value均不能为空
     * @param headers    请求头 key和value均不能为空
     * @param jsonParams 是否json格式传参
     */
    public static String post(String url, Map<String, Object> params, Map<String, String> headers, boolean jsonParams) {
        //参数构建
        Request.Builder requestBuilder = new Request.Builder();
        if (headers != null && !headers.isEmpty()) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                requestBuilder.addHeader(entry.getKey(), entry.getValue());
            }
        }

        RequestBody requestBody;
        if (jsonParams) {
            requestBuilder.addHeader("Content-Type", "application/json");
            requestBody = RequestBody.create(MediaType.get("application/json"), JSONObject.toJSONString(params));
        } else {
            FormBody.Builder formBodyBuilder = new FormBody.Builder();
            if (params != null && !params.isEmpty()) {
                for (Map.Entry<String, Object> entry : params.entrySet()) {
                    formBodyBuilder.add(entry.getKey(), String.valueOf(entry.getValue()));
                }
            }
            requestBody = formBodyBuilder.build();
            //TODO 其他类型请求参数
        }

        Request request = requestBuilder.url(url).post(requestBody).build();
        //执行HTTP请求
        try (Response response = okHttpClient.newCall(request).execute()) {
            ResponseBody body = response.body();
            if (response.isSuccessful()) {
                return body == null ? null : body.string();
            } else {
                log.error("请求失败 code={}, body={}", response.code(), body == null ? "" : body.string());
                return null;
            }
        } catch (IOException e) {
            log.error("请求执行失败 e={}", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 自定义请求重试次数
     */
    private static class MyRetryHandler implements Interceptor {

        public int maxRetry;//最大重试次数
        private int retryNum = 4;//假如设置为3次重试的话，则最大可能请求4次（默认1次+3次重试）

        public MyRetryHandler(int maxRetry) {
            this.maxRetry = maxRetry;
        }

        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            System.out.println("retryNum=" + retryNum);
            Response response = chain.proceed(request);
            while (!response.isSuccessful() && retryNum < maxRetry) {
                retryNum++;
                System.out.println("retryNum=" + retryNum);
                response = chain.proceed(request);
            }
            return response;
        }
    }

}
