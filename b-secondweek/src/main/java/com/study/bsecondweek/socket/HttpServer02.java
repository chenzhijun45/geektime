package com.study.bsecondweek.socket;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 为每个客户端请求创建一条线程处理
 */
public class HttpServer02 {

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(8802);
        while (true) {
            final Socket socket = serverSocket.accept();
            new Thread(() -> HttpServer01.service(socket)).start();
        }
    }

}
