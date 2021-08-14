package com.study.bsecondweek.socket;

import com.study.bsecondweek.utils.HttpClientUtil;
import com.study.bsecondweek.utils.HttpUtil;
import com.study.bsecondweek.utils.OkHttpUtil;

/**
 * 请求测试类
 */
public class Test {

    public static void main(String[] args) {
        String url = "http://localhost:8801";
//        String url = "http://localhost:8802";
//        String url = "http://localhost:8803";
//        String url = "http://127.0.0.1:8801";
//        String url = "http://127.0.0.1:8802";
//        String url = "http://127.0.0.1:8803";

        //netty
//        String url = "http://127.0.0.1:9999/test/sds";

        /**
         * HttpUtil 请求 8801到 8803 均成功，没有问题
         *
         * OkHttpUtil 请求 http://localhost:880x 均成功，没有问题
         * OkHttpUtil 请求 http://127.0.0.1:880x 仅8801成功，其他都有 Software caused connection abort: recv failed 异常
         *
         * HttpClientUtil 请求 localhost 和 127.0.0.1 都存在问题，Software caused connection abort: recv failed 异常的概率很大
         * 但是将 HttpClientUtil 的重试次数增加，却都可以请求成功获得 hello nio1
         * 或者将 HttpServer01.service(Socket socket)方法 睡眠10ms或更长时间 也可以成功
         *
         * 不是很明白其中原因，望老师解答。
         *
         * 网络情况：我电脑开了VPN，但是并未代理所有流量，直接请求火币或者币安的行情等API是会超时的，除非增加以下设置
         *    String proxyHost = "127.0.0.1";
         *    String proxyPort = "";//端口号取决于翻墙软件
         *
         *    // 对 https 开启代理
         *    System.setProperty("https.proxyHost", proxyHost);
         *    System.setProperty("https.proxyPort", proxyPort);
         */

//        System.out.println(HttpUtil.get(url));
//        System.out.println(OkHttpUtil.get(url));
        System.out.println(HttpClientUtil.get(url));

    }

}
