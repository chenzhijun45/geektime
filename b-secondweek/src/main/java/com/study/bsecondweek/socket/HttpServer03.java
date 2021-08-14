package com.study.bsecondweek.socket;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 线程池处理客户端请求
 */
public class HttpServer03 {

    public static void main(String[] args) throws IOException {
        //该线程池队列最大容量为Integer.MAX_VALUE 存在由于队列过大导致内存溢出问题 生产环境禁止使用
        ExecutorService threadPool = Executors.newFixedThreadPool(6);
        ServerSocket serverSocket = new ServerSocket(8803);
        while (true) {
            Socket socket = serverSocket.accept();
            threadPool.execute(() -> HttpServer01.service(socket));
        }
    }

}
