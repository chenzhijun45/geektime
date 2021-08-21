package com.study.cthirdweek.gateway.netty4;

import com.study.cthirdweek.gateway.httprequesthandler.HttpClientRequestHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 自定义channelHandler处理器，需要实现ChannelInboundHandlerAdapter类，才能被netty关联
 */
public class MyChannelHandler extends ChannelInboundHandlerAdapter {

    private static final Logger log = LoggerFactory.getLogger(MyChannelHandler.class);

    /**
     * 接收到客户端消息会调用此方法
     */
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        FullHttpRequest httpRequest = (FullHttpRequest) msg;
        //处理请求
        HttpClientRequestHandler.getInstance().handler(httpRequest, ctx);
        ctx.close();
    }

    /**
     * 给客户端发送消息会调用此方法
     */
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.writeAndFlush("hello 我是服务端");
    }


    /**
     * 发生异常时会调用此方法
     */
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable throwable) throws Exception {
        log.error("客户端连接发生异常 cause={} e={}", throwable.getCause(), throwable.getMessage());
        ctx.close();
    }

}
