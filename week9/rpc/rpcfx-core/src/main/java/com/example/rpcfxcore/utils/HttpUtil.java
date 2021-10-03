package com.example.rpcfxcore.utils;


import com.alibaba.fastjson.JSONObject;
import com.example.rpcfxcore.api.RpcFxRequest;
import lombok.extern.slf4j.Slf4j;

import javax.net.ssl.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 基于JDK实现接口调用
 */
@Slf4j
public class HttpUtil {

    private static final String CHARSET = "UTF-8";
    private static final String HTTP_POST = "POST";
    private static final String HTTP_GET = "GET";
    private static final String HTTP_PUT = "PUT";
    //请求连接超时时间
    private static final int connectTimeout = 5000;
    //数据传输超时时间 请求已经执行 但是耗时较久的请求 这里可以适当调大
    private static final int transferTimeout = 10000;

    private static final SSLSocketFactory sslSocketFactory = initSSLSocketFactory();
    private static final TrustAnyHostnameVerifier trustAnyHostnameVerifier = new TrustAnyHostnameVerifier();

    public static String get(String url) {
        return get(url, null, null);
    }

    public static String get(String url, Map<String, String> params) {
        return get(url, params, null);
    }

    public static String get(String url, Map<String, String> params, Map<String, String> headers) {
        HttpURLConnection conn = null;
        String responseStr;
        try {
            conn = getHttpConnection(buildUrlWithQueryString(url, params), HTTP_GET, headers);
            conn.connect();
            responseStr = readResponseString(conn);
        } catch (Exception var9) {
            throw new RuntimeException(var9);
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        return responseStr;
    }

    public static String post(String url, Map<String, Object> params) {
        return post(url, params, null);
    }

    public static String jsonPost(String url, RpcFxRequest params) {
        Map<String, String> headers = new HashMap<String, String>(1);
        headers.put("Content-Type", "application/json");
        return post(url, params, headers);
    }

    public static String jsonPost(String url, Map<String, Object> params) {
        Map<String, String> headers = new HashMap<String, String>(1);
        headers.put("Content-Type", "application/json");
        return post(url, params, headers);
    }

    public static String post(String url, Object params, Map<String, String> headers) {
        HttpURLConnection conn = null;
        String responseStr;
        try {
            conn = getHttpConnection(buildUrlWithQueryString(url, null), HTTP_POST, headers);
            conn.connect();
            OutputStream out = conn.getOutputStream();
            out.write(JSONObject.toJSONString(params).getBytes(CHARSET));
            out.flush();
            out.close();
            responseStr = readResponseString(conn);
        } catch (Exception e) {
            log.error("POST请求异常 e={}", e);
            throw new RuntimeException(e);
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        return responseStr;
    }


    public static String put(String url, Map<String, String> queryParas, String data, Map<String, String> headers) {
        HttpURLConnection conn = null;

        String var7;
        try {
            conn = getHttpConnection(buildUrlWithQueryString(url, queryParas), HTTP_PUT, headers);
            conn.connect();
            OutputStream out = conn.getOutputStream();
            out.write(data.getBytes(CHARSET));
            out.flush();
            out.close();
            var7 = readResponseString(conn);
        } catch (Exception var10) {
            throw new RuntimeException(var10);
        } finally {
            if (conn != null) {
                conn.disconnect();
            }

        }

        return var7;
    }

    public static String jsonPut(String url, String data) {
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/json");
        return put(url, null, data, headers);
    }

    private static SSLSocketFactory initSSLSocketFactory() {
        try {
            TrustManager[] tm = new TrustManager[]{new TrustAnyTrustManager()};
            SSLContext sslContext = SSLContext.getInstance("TLS", "SunJSSE");
            sslContext.init((KeyManager[]) null, tm, new SecureRandom());
            return sslContext.getSocketFactory();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static HttpURLConnection getHttpConnection(String url, String method, Map<String, String> headers) throws Exception {
        URL _url = new URL(url);
        HttpURLConnection conn = (HttpURLConnection) _url.openConnection();
        if (conn instanceof HttpsURLConnection) {
            ((HttpsURLConnection) conn).setSSLSocketFactory(sslSocketFactory);
            ((HttpsURLConnection) conn).setHostnameVerifier(trustAnyHostnameVerifier);
        }

        conn.setRequestMethod(method);
        conn.setDoOutput(true);
        conn.setDoInput(true);
        conn.setConnectTimeout(connectTimeout);
        conn.setReadTimeout(transferTimeout);
        conn.setUseCaches(false);
        if (headers != null) {
            String contentType = headers.get("Content-Type");
            if (contentType != null && contentType.trim().length() != 0) {
                conn.setRequestProperty("Content-Type", contentType);
            } else {
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
            }
        }

        conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.146 Safari/537.36");
        if (headers != null && !headers.isEmpty()) {
            Iterator<Entry<String, String>> iterator = headers.entrySet().iterator();
            while (iterator.hasNext()) {
                Entry<String, String> entry = iterator.next();
                conn.setRequestProperty(entry.getKey(), entry.getValue());
            }
        }
        return conn;
    }

    private static String readResponseString(HttpURLConnection conn) {
        StringBuilder sb = new StringBuilder();
        InputStream inputStream = null;
        try {
            if (conn.getResponseCode() == 500) {
                //获取500错误信息
                inputStream = conn.getErrorStream();
            } else {
                inputStream = conn.getInputStream();
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, CHARSET));
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            return sb.toString();
        } catch (IOException e) {
            log.error("读取返回数据异常 e={}", e.getMessage());
            throw new RuntimeException(e.getMessage());
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static String buildUrlWithQueryString(String url, Map<String, String> params) {
        if (params != null && !params.isEmpty()) {
            StringBuilder sb = new StringBuilder(url);
            boolean isFirst;
            if (!url.contains("?")) {
                isFirst = true;
                sb.append("?");
            } else {
                isFirst = false;
            }

            Iterator<Entry<String, String>> iterator = params.entrySet().iterator();
            while (iterator.hasNext()) {
                Entry<String, String> entry = iterator.next();
                if (isFirst) {
                    isFirst = false;
                } else {
                    sb.append("&");
                }

                String key = entry.getKey();
                String value = entry.getValue();
                if (value != null && value.trim().length() != 0) {
                    try {
                        value = URLEncoder.encode(value, CHARSET);
                    } catch (UnsupportedEncodingException e) {
                        log.error("参数构建异常 e={}", e);
                        throw new RuntimeException(e);
                    }
                    sb.append(key).append("=").append(value);
                }
            }
            return sb.toString();
        } else {
            return url;
        }
    }

    private static class TrustAnyHostnameVerifier implements HostnameVerifier {
        private TrustAnyHostnameVerifier() {
        }

        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    }

    private static class TrustAnyTrustManager implements X509TrustManager {
        private TrustAnyTrustManager() {
        }

        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }

        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }
    }

}
