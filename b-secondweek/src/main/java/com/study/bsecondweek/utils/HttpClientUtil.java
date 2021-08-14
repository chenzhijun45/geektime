package com.study.bsecondweek.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpMessage;
import org.apache.http.HttpRequest;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.springframework.lang.Nullable;

import javax.net.ssl.*;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.*;

/**
 * 基于HttpClient4.5.1实现接口调用 GET
 */
@Slf4j
public class HttpClientUtil {

    private static final String CHARSET = "UTF-8";
    //请求连接超时时间
    private static final int connectTimeout = 5000;
    //数据传输超时时间 请求已经执行 但是耗时较久的请求 这里可以适当调大
    private static final int transferTimeout = 10000;

    public static String get(String url) {
        return get(url, null, null);
    }

    public static String get(String url, Map<String, Object> params) {
        return get(url, params, null);
    }

    public static String get(String url, Map<String, Object> params, Map<String, String> headers) {
        CloseableHttpClient httpClient = createHttpClient();
        //拼接请求参数
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
        HttpGet get = new HttpGet(sb.toString());

        //设置请求头
        convertHeaders(get, headers);
        //请求超时
        get.setConfig(requestConfig());

        //执行请求，解析返回值
        String result = null;
        CloseableHttpResponse httpResponse = null;
        try {
            httpResponse = httpClient.execute(get);
            if (httpResponse != null) {
                result = EntityUtils.toString(httpResponse.getEntity());
            }
        } catch (IOException e) {
            log.error("当前请求执行失败 url={} params={} headers={} e={}", url, params, headers, e);
            throw new RuntimeException(e.getMessage());
        } finally {
            //关闭连接
            try {
                httpClient.close();
                if (httpResponse != null) {
                    httpResponse.close();
                }
                get.releaseConnection();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * 设置超时时间等
     */
    private static RequestConfig requestConfig() {
        //创建RequestConfig
        return RequestConfig.custom()
                .setConnectTimeout(connectTimeout)
                .setSocketTimeout(transferTimeout)
                .build();
    }

    /**
     * 构建 HttpClient 绕过SSL
     */
    private static CloseableHttpClient createHttpClient() {
        SSLContext ssl;
        try {
            ssl = createIgnoreVerifySSL();
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            log.error("构建 HttpClient SSLContext 异常 e={}", e);
            throw new RuntimeException(e.getMessage());
        }
        SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(ssl, NoopHostnameVerifier.INSTANCE);
        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder
                .<ConnectionSocketFactory>create()
                .register("http", PlainConnectionSocketFactory.INSTANCE)
                .register("https", socketFactory)
                .build();

        //创建ConnectionManager，添加Connection配置信息
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);

        return HttpClients.custom()
                .setConnectionManager(connectionManager)
                //TODO 注意：重试机制 默认3次 这里直接设置成1000次 是为了处理 Software caused connection abort: recv failed 异常
                .setRetryHandler(new MyRetryHandler())
                .build();
    }

    /**
     * 绕过SSL验证
     * 注意：对于非CA颁发的证书，可能存在安全问题
     */
    private static SSLContext createIgnoreVerifySSL() throws NoSuchAlgorithmException, KeyManagementException {
        SSLContext ssl = SSLContext.getInstance(SSLConnectionSocketFactory.TLS);
        // 实现一个X509TrustManager接口，用于绕过验证，不用修改里面的方法
        X509TrustManager trustManager = new X509TrustManager() {
            @Override
            public void checkClientTrusted(
                    java.security.cert.X509Certificate[] paramArrayOfX509Certificate,
                    String paramString) throws CertificateException {
            }

            @Override
            public void checkServerTrusted(
                    java.security.cert.X509Certificate[] paramArrayOfX509Certificate,
                    String paramString) throws CertificateException {
            }

            @Override
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
            }
        };
        ssl.init(null, new TrustManager[]{trustManager}, null);
        return ssl;
    }

    /**
     * 请求头信息换砖
     */
    private static void convertHeaders(HttpMessage httpMessage, @Nullable Map<String, String> headers) {
        if (headers == null) {
            headers = new HashMap<>();
        }
        headers.put("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.146 Safari/537.36");
        headers.put("Accept", "*/*");
        for (Map.Entry<String, String> header : headers.entrySet()) {
            httpMessage.addHeader(header.getKey(), header.getValue());
        }
    }

    /**
     * 自定义重试机制
     */
    private static class MyRetryHandler implements HttpRequestRetryHandler {
        @Override
        public boolean retryRequest(IOException exception, int executionCount, HttpContext context) {
            if (executionCount >= 1000) {// 如果已经重试了1000次，就放弃
                return false;
            }
            if (exception instanceof NoHttpResponseException) {// 如果服务器丢掉了连接，那么就重试
                return true;
            }
            if (exception instanceof SSLHandshakeException) {// 不要重试SSL握手异常
                return false;
            }
            if (exception instanceof InterruptedIOException) {// 超时
                return false;
            }
            if (exception instanceof UnknownHostException) {// 目标服务器不可达
                return false;
            }
            if (exception instanceof SSLException) {// SSL握手异常
                return false;
            }
            HttpClientContext clientContext = HttpClientContext
                    .adapt(context);
            HttpRequest request = clientContext.getRequest();
            // 如果请求是幂等的，就再次尝试
            return !(request instanceof HttpEntityEnclosingRequest);
        }
    }

}
