package com.study.cthirdweek.gateway.router;


import com.study.cthirdweek.gateway.utils.PropertiesUtil;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RouterStrategy {

    private static final Map<String, Router> map = new ConcurrentHashMap<>(4);

    static {
        map.put("RR", new RoundRobinRouter());
        map.put("LB", new LoadBalanceRouter());
        map.put("RAN", new RandomRouter());
    }

    /**
     * 获取实际访问的url
     */
    public static String getRouter() {
        Router router = map.get(PropertiesUtil.get("router.strategy", "RR"));
        String services = PropertiesUtil.get("real.servers.urls");
        List<String> routers = Arrays.asList(services.split(","));
        if (routers.size() <= 0) {
            throw new RuntimeException("real.servers cannot null");
        }

        String route = router.route(routers);
        if (route.endsWith("/")) {
            route = route.substring(0, route.length() - 1);
        }
        return route;
    }

}
