package com.study.cthirdweek.gateway.netty4;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;

/**
 * 自定义ChannelInitializer
 */
public class MyChannelInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel channel) throws Exception {
        ChannelPipeline pipeline = channel.pipeline();
        //HttpServerCodec作用：针对http请求编解码的处理类，但是无法处理请求体(POST)
        pipeline.addLast(new HttpServerCodec());
        //HttpObjectAggregator作用：处理请求体，将http请求聚合成一个FullHttpRequest或者FullHttpResponse
        pipeline.addLast(new HttpObjectAggregator(1024 * 1024));
        //设置自定义handler处理器
        pipeline.addLast(new MyChannelHandler());
    }

}
