package com.example.rpcfxcore.server;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.example.rpcfxcore.api.RpcFxRequest;
import com.example.rpcfxcore.api.RpcFxResolver;
import com.example.rpcfxcore.api.RpcFxResponse;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

public class RpcFxInvoker {

    private RpcFxResolver resolver;

    public RpcFxInvoker(RpcFxResolver resolver) {
        this.resolver = resolver;
    }

    public RpcFxResponse invoke(RpcFxRequest request) {
        RpcFxResponse response = new RpcFxResponse();
        String serviceClass = request.getServiceClass();

        // 作业1：改成泛型和反射
        //根据全类名获取目标对象
        Object service = resolver.resolve(serviceClass);//this.applicationContext.getBean(serviceClass);

        try {
            Method method = resolveMethodFromClass(service.getClass(), request.getMethod());
            Object result = method.invoke(service, request.getParams()); // dubbo, fastjson,
            // 两次json序列化能否合并成一个
            response.setResult(JSON.toJSONString(result, SerializerFeature.WriteClassName));
            response.setStatus(true);
            return response;
        } catch (IllegalAccessException | InvocationTargetException e) {

            // 3.Xstream

            // 2.封装一个统一的RpcfxException
            // 客户端也需要判断异常
            e.printStackTrace();
            response.setException(e);
            response.setStatus(false);
            return response;
        }
    }

    private Method resolveMethodFromClass(Class<?> klass, String methodName) {
        return Arrays.stream(klass.getMethods()).filter(m -> methodName.equals(m.getName())).findFirst().get();
    }

}
