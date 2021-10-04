package com.example.dubboorder.controller;

import com.example.dubbocommon.order.dto.OrderDTO;
import com.example.dubboorder.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("order")
public class OrderController {

    @Autowired
    private OrderService orderService;


    /**
     * 确认下单 生成订单 扣除账户余额
     */
    @PostMapping("confirm")
    public Object order(@RequestBody OrderDTO orderDTO) {
        return orderService.confirm(orderDTO);
    }


    /**
     * 确认下单 生成订单 扣除账户余额 异常情况测试
     * 订单生成执行成功 -> 账户余额扣减成功 -> 账户余额增加异常
     * ===>> 最终结果：全部回滚
     */
    @PostMapping("confirmException")
    public Object orderException(@RequestBody OrderDTO orderDTO) {
        return orderService.confirmException(orderDTO);
    }

}
