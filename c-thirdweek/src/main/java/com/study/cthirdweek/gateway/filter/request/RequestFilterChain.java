package com.study.cthirdweek.gateway.filter.request;

import io.netty.handler.codec.http.FullHttpRequest;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

/**
 * 过滤器链
 */
public class RequestFilterChain {

    private static final List<RequestFilter> reqF = new LinkedList<>();

    //Spring项目可以实现InitializingBean 在afterPropertiesSet()实现过滤器链的初始化添加 getBeansOfType获取子类
    static {
        reqF.add(new RequestFilterA());
        reqF.add(new RequestFilterB());
    }


    public static void beforeHandler(FullHttpRequest request) {
        //排序
        reqF.sort(Comparator.comparing(RequestFilter::getOrder));
        //执行
        for (RequestFilter r : reqF) {
            r.beforeFilter(request);
        }
    }

}
