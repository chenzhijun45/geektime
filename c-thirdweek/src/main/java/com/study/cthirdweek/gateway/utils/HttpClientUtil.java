package com.study.cthirdweek.gateway.utils;

import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpMessage;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.AuthSchemes;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.*;

public class HttpClientUtil {

    private static final Logger log = LoggerFactory.getLogger(HttpClientUtil.class);

    //请求连接超时时间
    private static final int connectTimeout = 5000;
    //数据传输超时时间
    private static final int transferTimeout = 10000;


    public static HttpResponse get(String url) {
        return get(url, null, null);
    }

    public static HttpResponse get(String url, Map<String, Object> params) {
        return get(url, params, null);
    }

    public static HttpResponse post(String url, Map<String, Object> params) {
        return post(url, params, null);
    }

    public static HttpResponse jsonPost(String url, Map<String, Object> params) {
        return jsonPost(url, params, null);
    }

    public static HttpResponse put(String url, Map<String, Object> params) {
        return put(url, params, null);
    }

    public static HttpResponse jsonPut(String url, Map<String, Object> params) {
        return jsonPut(url, params, null);
    }

    public static HttpResponse get(String url, Map<String, Object> params, Map<String, String> headers) {
        HttpClient httpClient = createHttpClient();
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

        //执行请求，解析返回值
        try {
            return httpClient.execute(get);
        } catch (IOException e) {
            log.error("当前请求执行失败 url={} params={} headers={} e={}", url, params, headers, e);
            throw new RuntimeException(e.getMessage());
        }
    }

    public static HttpResponse post(String url, Map<String, Object> params, Map<String, String> headers) {
        //创建post请求
        HttpClient httpClient = createHttpClient();
        HttpPost post = new HttpPost(url);

        headers.remove("Content-Length");
        //设置请求头
        convertHeaders(post, headers);

        //请求参数
        List<NameValuePair> list = new LinkedList<>();
        if (params != null && !params.isEmpty()) {
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                list.add(new BasicNameValuePair(entry.getKey() == null ? "" : entry.getKey(), entry.getValue() == null ? "" : entry.getValue().toString()));
            }
        }
        try {
            UrlEncodedFormEntity httpEntity = new UrlEncodedFormEntity(list, "UTF-8");
            post.setEntity(httpEntity);

            //执行请求，解析返回值
            return httpClient.execute(post);
        } catch (IOException e) {
            log.error("当前请求执行失败 url={} params={} headers={} e={}", url, params, headers, e);
            throw new RuntimeException(e.getMessage());
        }
    }

    public static HttpResponse jsonPost(String url, String jsonParams, Map<String, String> headers) {
        //创建post请求
        HttpClient httpClient = createHttpClient();
        HttpPost post = new HttpPost(url);

        //httpClient 当json格式传参时，会自动设置Content-Length 这里不要重复设置 否则会异常
        headers.remove("Content-Length");

        //设置请求头
        convertHeaders(post, headers);

        StringEntity stringEntity = new StringEntity(jsonParams, ContentType.APPLICATION_JSON);
        post.setEntity(stringEntity);

        //执行请求，解析返回值
        try {
            return httpClient.execute(post);
        } catch (Exception e) {
            log.error("当前请求执行失败 url={} params={} headers={} e={}", url, jsonParams, headers, e);
            throw new RuntimeException(e.getCause().getMessage());
        }
    }

    public static HttpResponse jsonPost(String url, Map<String, Object> params, Map<String, String> headers) {
        //创建post请求
        HttpClient httpClient = createHttpClient();
        HttpPost post = new HttpPost(url);

        //设置请求头
        convertHeaders(post, headers);

        //这是一个空的json
        String jsonParams = "{}";
        if (params != null && !params.isEmpty()) {
            jsonParams = JSONObject.toJSONString(params);
        }
        StringEntity stringEntity = new StringEntity(jsonParams, ContentType.APPLICATION_JSON);
        post.setEntity(stringEntity);

        //执行请求，解析返回值
        try {
            return httpClient.execute(post);
        } catch (IOException e) {
            log.error("当前请求执行失败 url={} params={} headers={} e={}", url, params, headers, e);
            throw new RuntimeException(e.getCause().getMessage());
        }
    }

    public static HttpResponse put(String url, Map<String, Object> params, Map<String, String> headers) {
        //创建post请求
        HttpClient httpClient = createHttpClient();
        HttpPut put = new HttpPut(url);

        //设置请求头
        convertHeaders(put, headers);

        //请求参数
        List<NameValuePair> list = new LinkedList<>();
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            list.add(new BasicNameValuePair(entry.getKey() == null ? "" : entry.getKey(), entry.getValue() == null ? "" : entry.getValue().toString()));
        }
        try {
            UrlEncodedFormEntity httpEntity = new UrlEncodedFormEntity(list, "UTF-8");
            put.setEntity(httpEntity);

            //执行请求，解析返回值
            return httpClient.execute(put);
        } catch (IOException e) {
            log.error("当前请求执行失败 url={} params={} headers={} e={}", url, params, headers, e);
            throw new RuntimeException(e.getMessage());
        }
    }

    public static HttpResponse jsonPut(String url, Map<String, Object> params, Map<String, String> headers) {
        //创建post请求
        HttpClient httpClient = createHttpClient();
        HttpPut put = new HttpPut(url);

        //设置请求头
        convertHeaders(put, headers);

        //这是一个空的json
        String jsonParams = "{}";
        if (params != null && !params.isEmpty()) {
            jsonParams = JSONObject.toJSONString(params);
        }
        StringEntity stringEntity = new StringEntity(jsonParams, ContentType.APPLICATION_JSON);
        put.setEntity(stringEntity);

        //执行请求，解析返回值
        try {
            return httpClient.execute(put);
        } catch (IOException e) {
            log.error("当前请求执行失败 url={} params={} headers={} e={}", url, params, headers, e);
            throw new RuntimeException(e.getMessage());
        }
    }


    /**
     * 构建 HttpClient 绕过SSL
     */
    private static HttpClient createHttpClient() {
        //创建RequestConfig
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(connectTimeout)
                .setSocketTimeout(transferTimeout)
                .setCookieSpec(CookieSpecs.STANDARD_STRICT)
                .setExpectContinueEnabled(Boolean.TRUE)
                .setTargetPreferredAuthSchemes(Arrays.asList(AuthSchemes.NTLM, AuthSchemes.DIGEST))
                .setProxyPreferredAuthSchemes(Collections.singletonList(AuthSchemes.BASIC)).build();

        X509TrustManager trustManager = new X509TrustManager() {
            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            @Override
            public void checkClientTrusted(X509Certificate[] xcs, String str) {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] xcs, String str) {
            }
        };
        SSLContext sslContext;
        try {
            sslContext = SSLContext.getInstance(SSLConnectionSocketFactory.SSL);
            sslContext.init(null, new TrustManager[]{trustManager}, null);
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            log.error("构建HttpClient异常 e={}", e);
            throw new RuntimeException(e.getMessage());
        }

        SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE);

        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder
                .<ConnectionSocketFactory>create()
                .register("http", PlainConnectionSocketFactory.INSTANCE)
                .register("https", socketFactory)
                .build();

        //创建ConnectionManager，添加Connection配置信息
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);

        return HttpClients.custom()
                .setConnectionManager(connectionManager)
                .setDefaultRequestConfig(requestConfig)
                .build();
    }


    /**
     * 请求头信息换砖
     */
    private static void convertHeaders(HttpMessage httpMessage, Map<String, String> headers) {
        if (headers != null && !headers.isEmpty()) {
            for (Map.Entry<String, String> header : headers.entrySet()) {
                if (!httpMessage.containsHeader(header.getKey())){
                httpMessage.setHeader(header.getKey(), header.getValue());
                }
            }
        }
    }

}
