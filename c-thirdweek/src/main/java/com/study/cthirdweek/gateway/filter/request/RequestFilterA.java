package com.study.cthirdweek.gateway.filter.request;

import io.netty.handler.codec.http.FullHttpRequest;

public class RequestFilterA implements RequestFilter {

    @Override
    public void beforeFilter(FullHttpRequest request) {
        System.out.println("请求执行前处理，我是过滤器A");
    }

    @Override
    public int getOrder() {
        return 10;
    }

}
