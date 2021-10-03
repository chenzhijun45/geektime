package com.example.rpcfxcore.api;

import lombok.Data;

@Data
public class RpcFxRequest {

    /**
     * 请求服务类 全路径
     */
    private String serviceClass;

    /**
     * 请求方法
     */
    private String method;

    /**
     * 请求参数
     */
    private Object[] params;

}
