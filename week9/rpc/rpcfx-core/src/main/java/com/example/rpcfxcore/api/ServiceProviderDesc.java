package com.example.rpcfxcore.api;

import lombok.Builder;
import lombok.Data;

/**
 * 服务提供者描述类
 */
@Data
@Builder
public class ServiceProviderDesc {

    /**
     * 服务host
     */
    private String host;

    /**
     * 服务端口
     */
    private Integer port;

    /**
     * 提供服务的类 全类名
     */
    private String serviceClass;

    // group
    // version

}
