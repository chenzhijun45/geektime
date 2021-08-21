package com.study.cthirdweek.gateway.router;

import java.util.List;

/**
 * 轮询
 */
public class RoundRobinRouter implements Router {

    //本次调用服务在数组中的位置
    private int position = 0;

    @Override
    public String route(List<String> list) {
        String router;
        //保证同一时刻只能有一个线程修改position 缺点是降低了并发吞吐量
        synchronized (this) {
            router = list.get(position);
            if (position == (list.size() - 1)) {
                position = 0;
            } else {
                position++;
            }
        }

        System.out.println("router=" + router);

        return router;
    }

}
