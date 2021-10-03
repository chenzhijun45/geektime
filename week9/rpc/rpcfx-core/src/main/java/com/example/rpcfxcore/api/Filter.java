package com.example.rpcfxcore.api;

/**
 * 请求过滤器
 */
public interface Filter {

    boolean filter(RpcFxRequest request);
}
