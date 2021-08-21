package com.study.cthirdweek.gateway.filter.response;

import io.netty.handler.codec.http.FullHttpResponse;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class ResponseFilterChain {

    private static final List<ResponseFilter> resF = new LinkedList<>();

    //Spring项目可以实现InitializingBean 在afterPropertiesSet()实现过滤器链的初始化添加 getBeansOfType获取子类
    static {
        resF.add(new ResponseFilterA());
        resF.add(new ResponseFilterB());
    }


    public static void afterHandler(FullHttpResponse response) {
        //排序
        resF.sort(Comparator.comparing(ResponseFilter::getOrder));
        //执行
        for (ResponseFilter r : resF) {
            r.afterFilter(response);
        }
    }

}
