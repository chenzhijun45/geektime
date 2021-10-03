package com.example.rpcfxcore.api;

import java.util.List;

/**
 * 服务调用负载均衡算法
 */
public interface LoadBalance {

    String select(List<String> urls);

}
