package com.example.rpxfxprovider;

import com.example.rpcfxapi.OrderService;
import com.example.rpcfxapi.UserService;
import com.example.rpcfxcore.api.RpcFxRequest;
import com.example.rpcfxcore.api.RpcFxResolver;
import com.example.rpcfxcore.api.RpcFxResponse;
import com.example.rpcfxcore.server.RpcFxInvoker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@SpringBootApplication
public class RpxfxProviderApplication {

    public static void main(String[] args) {
        SpringApplication.run(RpxfxProviderApplication.class, args);
    }


    @Autowired
    private RpcFxInvoker invoker;

    @PostMapping("/")
    public RpcFxResponse invoke(@RequestBody RpcFxRequest request) {
        return invoker.invoke(request);
    }

    @Bean
    public RpcFxInvoker createInvoker(@Autowired RpcFxResolver resolver){
        return new RpcFxInvoker(resolver);
    }

    @Bean
    public RpcFxResolver createResolver(){
        return new DemoResolver();
    }

    @Bean(name = "com.example.rpcfxapi.UserService")
    public UserService createUserService(){
        return new UserServiceImpl();
    }

    @Bean(name = "com.example.rpcfxapi.OrderService")
    public OrderService createOrderService(){
        return new OrderServiceImpl();
    }

}
