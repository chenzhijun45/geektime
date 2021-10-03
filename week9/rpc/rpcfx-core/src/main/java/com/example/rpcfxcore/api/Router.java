package com.example.rpcfxcore.api;

import java.util.List;

/**
 * 路由
 */
public interface Router {

    List<String> route(List<String> urls);

}
