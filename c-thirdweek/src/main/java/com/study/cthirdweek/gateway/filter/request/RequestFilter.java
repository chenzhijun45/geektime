package com.study.cthirdweek.gateway.filter.request;

import io.netty.handler.codec.http.FullHttpRequest;

public interface RequestFilter {

    void beforeFilter(FullHttpRequest request);

    //排序 越小越靠前
    int getOrder();
}
