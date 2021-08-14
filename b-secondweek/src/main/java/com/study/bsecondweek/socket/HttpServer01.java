package com.study.bsecondweek.socket;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 主线程处理所有客户端请求
 */
@Slf4j
public class HttpServer01 {

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(8801);
        while (true) {
            Socket socket = serverSocket.accept();
            service(socket);
        }

    }

    public static void service(Socket socket) {
        try {
            System.out.println(Thread.currentThread().getName() + "=======客户端请求进来了=======");
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
            writer.println("HTTP/1.1 200 OK");
            writer.println("Content-Type:text/html;character=utf-8");
            String body = "hello nio1";
            writer.println("Content-Length:" + body.getBytes().length);
            writer.println();
            writer.write(body);
            /**
             * TODO 说明：如果是HttpClientUtils和OkHttpUtil发起请求 这里可能存在 Software caused connection abort: recv failed 异常
             *            HttpUtil发起请求不存在异常，都可以成功。
             *            查了一圈，说是因为服务端关闭了连接，此时客户端再起发起请求，所以导致这个问题
             *            但是为什么客户端会再次发起请求呢？
             *            个人猜测是因为数据没获取完，所以再次发起请求希望获取 "hello nio1"
             *            据此，我想了两个解决办法：
             *                  1：此处线程睡眠10ms(更长都行)等待客户端获取完数据，问题解决；
             *                  2：不断发起重试，直到成功，问题解决；HttpClientUtil 最大重试1000次，OkHttpUtil暂未处理
             *            但是为什么会存在数据没写完问题，或者是其他问题导致的，希望老师解答，谢谢。
             */

            //线程睡眠10ms 可以保证请求均成功获取到 "hello nio1"
            //Thread.sleep(10);
            writer.close();
            socket.close();
        } catch (Exception e) {
            log.error("连接异常 e={}", e.getMessage());
        }
    }

}
