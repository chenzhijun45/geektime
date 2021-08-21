package com.study.cthirdweek.gateway.router;


import java.util.List;
import java.util.Random;

/**
 * 随机选择一个服务
 */
public class RandomRouter implements Router {

    @Override
    public String route(List<String> list) {
        return list.get(new Random().nextInt(list.size()));
    }

}
