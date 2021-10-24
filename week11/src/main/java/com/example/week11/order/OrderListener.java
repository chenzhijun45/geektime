package com.example.week11.order;

import com.alibaba.fastjson.JSONObject;
import com.example.week11.config.RedisConfig;
import com.example.week11.controller.TestController;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

/**
 * 下单信息订阅者 负责异步处理订单
 * 假设订单处理需要600ms
 */
@Component
public class OrderListener implements MessageListener {


    @Override
    public void onMessage(Message message, byte[] pattern) {
        System.out.println("收到订单消息啦...");
        Object orderDeserialize = RedisConfig.valueSerializer.deserialize(message.getBody());
        TestController.Order order = JSONObject.parseObject(orderDeserialize.toString(), TestController.Order.class);

        System.out.println("订单id=" + order.getOrderId());
        System.out.println("用户id=" + order.getUserId());

        try {
            Thread.sleep(600);
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("订单信息处理完毕");
    }

}
