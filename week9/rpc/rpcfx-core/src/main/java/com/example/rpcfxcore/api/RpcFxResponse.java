package com.example.rpcfxcore.api;

import lombok.Data;

@Data
public class RpcFxResponse {

    /**
     * 请求返回值
     */
    private Object result;

    /**
     * 请求返回状态 true：请求成功返回   false：请求异常返回
     */
    private boolean status;

    /**
     * 请求返回异常 如果有异常
     */
    private Exception exception;

}
