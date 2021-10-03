package com.example.rpcfxconsumer;

import com.example.rpcfxapi.Order;
import com.example.rpcfxapi.OrderService;
import com.example.rpcfxapi.User;
import com.example.rpcfxapi.UserService;
import com.example.rpcfxcore.client.RpcFxCglib;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RpcfxConsumerApplication {

    public static void main(String[] args) {

        UserService userService = RpcFxCglib.create(UserService.class, "http://localhost:8081/");
        User user = userService.findById(1);
        System.out.println("userService RPC服务调用返回结果：" + user.getName());


        OrderService orderService = RpcFxCglib.create(OrderService.class, "http://localhost:8081/");
        Order order = orderService.findOrderById(1);
        System.out.println("orderService RPC服务调用返回结果：" + order.getName());
        System.out.println("orderService RPC服务调用返回结果：" + order.getAmount());
//        SpringApplication.run(RpcfxConsumerApplication.class, args);

    }

}
