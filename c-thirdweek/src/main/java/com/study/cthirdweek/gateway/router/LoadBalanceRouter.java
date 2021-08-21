package com.study.cthirdweek.gateway.router;

import com.study.cthirdweek.gateway.utils.PropertiesUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 负载均衡
 */
@Slf4j
public class LoadBalanceRouter implements Router {


    //本次调用服务在数组中的位置
    private int position = 0;

    @Override
    public String route(List<String> list) {
        String weights = PropertiesUtil.get("real.servers.weights", "");
        List<String> weightList = Arrays.asList(weights.split(","));
        if (weightList.size() != list.size()) {
            log.error("配置服务数量：{}  对于权重数量：{}", list.size(), weightList.size());
            throw new RuntimeException("server weight error");
        }

        //TODO 待优化

        List<String> tempList = new ArrayList<>();
        for (String server : list) {
            for (int i = 0; i < Integer.parseInt(weightList.get(i)); i++) {
                tempList.add(server);
            }
        }

        String router;
        synchronized (this) {
            router = tempList.get(position);
            if (position == (list.size() - 1)) {
                position = 0;
            } else {
                position++;
            }
        }
        return router;
    }

}
