package com.example.week11.controller;

import com.alibaba.fastjson.JSONObject;
import com.example.week11.service.RedisService;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Locale;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RestController
public class TestController {

    @Autowired
    private RedisService redisService;


    /**
     * 获取计数器结果
     */
    @GetMapping("counter")
    public long getCounter() {
        return redisService.counter();
    }


    public static final String LOCK_KEY = "lock:test";
    public static final String LOCK_VALUE = "123456789";

    /**
     * 加锁简单测试
     */
    @GetMapping("redisLock")
    public String lockTest() {
        boolean lock = redisService.lock(LOCK_KEY, LOCK_VALUE, 30, TimeUnit.SECONDS);
        try {
            if (lock) {
                //TODO doSomeThing
                return "redis分布式锁获取成功";
            } else {
                return "redis分布式锁获取失败";
            }
        } finally {
            //解锁
            //redisService.unlock(LOCK_KEY, LOCK_VALUE);
        }
    }


    /**
     * 解锁简单测试
     */
    @GetMapping("redisUnlock")
    public String unlockTest() {
        boolean lock = redisService.unlock(LOCK_KEY, LOCK_VALUE);
        if (lock) {
            return "redis分布式锁解锁成功";
        }
        //解锁失败原因：1-系统异常  2-锁不存在(已过期)  3-不是自己的锁
        return "redis分布式锁解锁失败";
    }


    public static final String ORDER_CHANNEL = "msg:order";

    /**
     * 模拟基于 Redis 的 PubSub 实现订单异步处理
     * 下单
     */
    @GetMapping("order")
    public String order() {
        String orderId = UUID.randomUUID().toString().replace("-", "").toLowerCase(Locale.ROOT);
        Order order = new Order(orderId, "userId-" + new Random().nextInt(1000));
        String jsonStr = JSONObject.toJSONString(order);
        redisService.convertAndSend(ORDER_CHANNEL, jsonStr);
        return "订单处理中，订单id=" + orderId;
    }

    @Data
    @AllArgsConstructor
    public static class Order {
        private String orderId;
        private String userId;
    }

}
