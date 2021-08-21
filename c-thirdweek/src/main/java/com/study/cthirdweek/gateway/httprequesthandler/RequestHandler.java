package com.study.cthirdweek.gateway.httprequesthandler;

import com.study.cthirdweek.gateway.router.RouterStrategy;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;

/**
 * 请求处理接口
 */
public interface RequestHandler {

    //处理HTTP请求
    void handler(FullHttpRequest request, ChannelHandlerContext ctx);

    //获取实际请求URL
    default String getRealUrl(FullHttpRequest request) {
        //获取实际访问的地址
        String serverUrl = RouterStrategy.getRouter();
        return serverUrl + request.uri();
    }

}
