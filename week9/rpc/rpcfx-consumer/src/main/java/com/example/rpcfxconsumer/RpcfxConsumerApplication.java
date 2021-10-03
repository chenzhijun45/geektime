package com.example.rpcfxconsumer;

import com.example.rpcfxapi.User;
import com.example.rpcfxapi.UserService;
import com.example.rpcfxcore.client.RpcFx;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RpcfxConsumerApplication {

    public static void main(String[] args) {

        UserService userService = RpcFx.create(UserService.class, "http://localhost:8081/");
        User user = userService.findById(1);

        System.out.println("RPC服务调用返回结果：" + user.getName());
//        SpringApplication.run(RpcfxConsumerApplication.class, args);

    }

}
