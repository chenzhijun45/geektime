package com.study.cthirdweek.gateway.filter.response;

import io.netty.handler.codec.http.FullHttpResponse;

public class ResponseFilterA implements ResponseFilter {

    @Override
    public void afterFilter(FullHttpResponse response) {
        System.out.println("我是after过滤器A");
        response.headers().add("hello", "netty");
    }

    @Override
    public int getOrder() {
        return 1;
    }

}
