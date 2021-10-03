package com.example.rpcfxcore.api;

public interface RpcFxResolver {

    /**
     * 根据服务类 全类名 获取具体的服务实例
     */
    Object resolve(String serviceClass);
}
