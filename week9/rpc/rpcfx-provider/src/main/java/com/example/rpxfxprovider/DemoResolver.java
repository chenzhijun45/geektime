package com.example.rpxfxprovider;

import com.example.rpcfxcore.api.RpcFxResolver;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class DemoResolver implements RpcFxResolver, ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public Object resolve(String serviceClass) {

        System.out.println("serviceClass=>"+serviceClass);

        return this.applicationContext.getBean(serviceClass);
    }
}
