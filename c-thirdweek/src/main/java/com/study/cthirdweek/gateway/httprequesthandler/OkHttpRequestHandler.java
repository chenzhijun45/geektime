package com.study.cthirdweek.gateway.httprequesthandler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;

public class OkHttpRequestHandler implements RequestHandler {

    private static volatile OkHttpRequestHandler okHttpRequestHandler = null;

    private OkHttpRequestHandler() {
    }

    public static OkHttpRequestHandler getInstance() {
        if (okHttpRequestHandler == null) {
            synchronized (OkHttpRequestHandler.class) {
                if (okHttpRequestHandler == null) {
                    okHttpRequestHandler = new OkHttpRequestHandler();
                }
            }
        }
        return okHttpRequestHandler;
    }


    @Override
    public void handler(FullHttpRequest request, ChannelHandlerContext ctx) {
        String realUrl = getRealUrl(request);
        //TODO
    }

}
