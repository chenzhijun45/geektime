package com.study.cthirdweek.gateway.filter.response;

import io.netty.handler.codec.http.FullHttpResponse;

public interface ResponseFilter {

    void afterFilter(FullHttpResponse response);

    //排序 越小越靠前
    int getOrder();
}
