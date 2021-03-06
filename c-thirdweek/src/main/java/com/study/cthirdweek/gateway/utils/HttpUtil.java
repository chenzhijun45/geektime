package com.study.cthirdweek.gateway.utils;

import com.alibaba.fastjson.JSONObject;

import javax.net.ssl.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class HttpUtil {

    private static final String HTTP_POST = "POST";
    private static final String HTTP_GET = "GET";
    private static final String HTTP_PUT = "PUT";

    public static final int connectTimeout = 10000;
    public static final int readTimeout = 10000;

    private static final SSLSocketFactory sslSocketFactory = initSSLSocketFactory();
    private static final TrustAnyHostnameVerifier trustAnyHostnameVerifier = new TrustAnyHostnameVerifier();


    public static String get(String url, Map<String, String> queryParas, Map<String, String> headers) {
        HttpURLConnection conn = null;
        String var6;
        try {
            conn = getHttpConnection(buildUrlWithQueryString(url, queryParas), HttpUtil.HTTP_GET, headers);
            conn.connect();
            var6 = readResponseString(conn);
        } catch (Exception var9) {
            throw new RuntimeException(var9);
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        return var6;
    }

    public static String get(String url, Map<String, String> queryParas) {
        return get(url, queryParas, null);
    }

    public static String get(String url) {
        return get(url, null, null);
    }

    public static String jsonGet(String url) {
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/json");
        return get(url, null, headers);
    }

    public static String jsonGet(String url, Map<String, String> params) {
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/json");
        return get(url, params, headers);
    }

    public static String jsonGet(String url, Map<String, String> params, Map<String, String> headers) {
        headers.put("Content-Type", "application/json");
        return get(url, params, headers);
    }

    public static String post(String url, Map<String, String> queryParas, String data, Map<String, String> headers) {
        HttpURLConnection conn = null;
        String var7;
        try {
            conn = getHttpConnection(buildUrlWithQueryString(url, queryParas), HttpUtil.HTTP_POST, headers);
            conn.connect();
            OutputStream out = conn.getOutputStream();
            out.write(data.getBytes(StandardCharsets.UTF_8));
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

    public static String post(String url, Map<String, String> queryParas) {
        return post(url, queryParas, "", null);
    }

    public static String post(String url, Map<String, String> queryParas, String data) {
        return post(url, queryParas, data, (Map) null);
    }

    public static String post(String url, String data, Map<String, String> headers) {
        return post(url, (Map) null, data, headers);
    }

    public static String post(String url, String data) {
        return post(url, (Map) null, data, (Map) null);
    }

    public static String jsonPost(String url, String data, Map<String, Object> head) {
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/json");
        headers.put("Authorization", head.get("Authorization").toString());
        return post(url, (Map) null, data, headers);
    }

    public static String jsonPost(String url, Map<String, Object> params) {
        return HttpUtil.jsonPost(url, null, JSONObject.toJSONString(params));
    }

    public static String jsonPost(String url, Map<String, String> headers, String data) {
        if (headers == null) {
            headers = new HashMap<String, String>();
        }

        ((Map) headers).put("Content-Type", "application/json");
        return post(url, (Map) null, data, (Map) headers);
    }

    private static String put(String url, Map<String, String> queryParas, String data, Map<String, String> headers) {
        HttpURLConnection conn = null;
        String var7;
        try {
            conn = getHttpConnection(buildUrlWithQueryString(url, queryParas), HttpUtil.HTTP_PUT, headers);
            conn.connect();
            OutputStream out = conn.getOutputStream();
            out.write(data.getBytes(StandardCharsets.UTF_8));
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
        Map<String, String> headers = new HashMap();
        headers.put("Content-Type", "application/json");
        return put(url, (Map) null, data, headers);
    }

    private static SSLSocketFactory initSSLSocketFactory() {
        try {
            TrustManager[] tm = new TrustManager[]{new TrustAnyTrustManager()};
            SSLContext sslContext = SSLContext.getInstance("TLS", "SunJSSE");
            sslContext.init((KeyManager[]) null, tm, new SecureRandom());
            return sslContext.getSocketFactory();
        } catch (Exception var2) {
            throw new RuntimeException(var2);
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
        conn.setReadTimeout(readTimeout);
        conn.setUseCaches(false);
        if (headers != null) {
            String contentType = (String) headers.get("Content-Type");
            if (contentType != null && !contentType.trim().equals("")) {
                conn.setRequestProperty("Content-Type", contentType);
            } else {
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
            }
        }

        conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.146 Safari/537.36");
        if (headers != null && !headers.isEmpty()) {
            Iterator var6 = headers.entrySet().iterator();
            while (var6.hasNext()) {
                Map.Entry<String, String> entry = (Map.Entry) var6.next();
                conn.setRequestProperty((String) entry.getKey(), (String) entry.getValue());
            }
        }
        return conn;
    }

    private static String readResponseString(HttpURLConnection conn) throws Exception {
        StringBuilder sb = new StringBuilder();
        InputStream inputStream = null;
        try {
            if (conn.getResponseCode() == 500) {
                //??????500????????????
                inputStream = conn.getErrorStream();
            } else {
                inputStream = conn.getInputStream();
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            return sb.toString();
        } catch (Exception var13) {
            throw new RuntimeException(var13);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException var12) {
                    var12.printStackTrace();
                }
            }
        }
    }

    private static String buildUrlWithQueryString(String url, Map<String, String> queryParas) {
        if (queryParas != null && !queryParas.isEmpty()) {
            StringBuilder sb = new StringBuilder(url);
            boolean isFirst;
            if (!url.contains("?")) {
                isFirst = true;
                sb.append("?");
            } else {
                isFirst = false;
            }

            for (Map.Entry<String, String> stringStringEntry : queryParas.entrySet()) {
                if (isFirst) {
                    isFirst = false;
                } else {
                    sb.append("&");
                }

                String key = stringStringEntry.getKey();
                String value = stringStringEntry.getValue();
                if (value != null && !value.trim().equals("")) {
                    try {
                        value = URLEncoder.encode(value, "UTF-8");
                    } catch (UnsupportedEncodingException var9) {
                        throw new RuntimeException(var9);
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
