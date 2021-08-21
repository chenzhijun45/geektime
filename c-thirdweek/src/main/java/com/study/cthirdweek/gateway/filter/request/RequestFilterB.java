package com.study.cthirdweek.gateway.filter.request;

import io.netty.handler.codec.http.FullHttpRequest;

public class RequestFilterB implements RequestFilter {

    @Override
    public void beforeFilter(FullHttpRequest request) {
        System.out.println("请求执行前处理，我是过滤器B");
        request.headers().add("geektime", "go");
    }

    @Override
    public int getOrder() {
        return 1;
    }

}
