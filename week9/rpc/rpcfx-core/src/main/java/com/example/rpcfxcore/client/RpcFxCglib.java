package com.example.rpcfxcore.client;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.ParserConfig;
import com.example.rpcfxcore.api.*;
import com.example.rpcfxcore.utils.HttpUtil;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * 通过cglib实现动态代理
 */
public final class RpcFxCglib {

    static {
        ParserConfig.getGlobalInstance().addAccept("com.example");
    }

    public static <T, filters> T createFromRegistry(final Class<T> serviceClass,
                                                    final String zkUrl,
                                                    Router router,
                                                    LoadBalance loadBalance,
                                                    Filter filter) {
        // 加filte之一
        // curator Provider list from zk
        List<String> invokers = new ArrayList<>();
        // 1. 简单：从zk拿到服务提供的列表
        // 2. 挑战：监听zk的临时节点，根据事件更新这个list（注意，需要做个全局map保持每个服务的提供者List）

        List<String> urls = router.route(invokers);

        //负载均衡获取具体调用的服务url
        String url = loadBalance.select(urls); // router, loadbalance

        return (T) create(serviceClass, url, filter);

    }

    public static <T> T create(final Class<T> serviceClass, final String url, Filter... filters) {
        /**
         * cglib创建子类对目标对象进行增强(代理)
         */
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(serviceClass);
        enhancer.setCallback(new MyInterceptor(serviceClass, url, filters));
        return (T) enhancer.create();
    }


    /**
     * cglib代理类方法
     */
    public static class MyInterceptor implements MethodInterceptor {
        private final Class<?> serviceClass;
        private final String url;
        private final Filter[] filters;

        public <T> MyInterceptor(Class<T> serviceClass, String url, Filter... filters) {
            this.serviceClass = serviceClass;
            this.url = url;
            this.filters = filters;
        }

        @Override
        public Object intercept(Object obj, Method method, Object[] params, MethodProxy proxy) throws Throwable {

            RpcFxRequest request = new RpcFxRequest();
            request.setServiceClass(this.serviceClass.getName());
            request.setMethod(method.getName());
            request.setParams(params);

            if (null != filters) {
                for (Filter filter : filters) {
                    if (!filter.filter(request)) {
                        return null;
                    }
                }
            }

            System.out.println("请求参数=" + request);
            String result = HttpUtil.jsonPost(url, request);
            System.out.println("请求返回值=" + result);
            RpcFxResponse response = JSON.parseObject(result, RpcFxResponse.class);

            // 加filter地方之三
            // Student.setTeacher("cuijing");

            // 这里判断response.status，处理异常
            // 考虑封装一个全局的RpcFxException

            return JSON.parse(response.getResult().toString());
        }
    }

}
