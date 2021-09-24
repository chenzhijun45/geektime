package com.study.week8.controller;

import com.study.week8.controller.request.OrderQueryRequest;
import com.study.week8.controller.response.OrderResponse;
import com.study.week8.controller.response.PageResult;
import com.study.week8.controller.response.R;
import com.study.week8.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
@RequestMapping("order")
public class OrderController {

    @Autowired
    private OrderService orderService;


    //新增订单
    @PostMapping("addOrder")
    public R<OrderResponse> addOrder() {
        return orderService.createOrder();
    }

    //分页查询订单
    @GetMapping("queryList")
    public R<PageResult<OrderResponse>> queryList(OrderQueryRequest request) {
        return orderService.queryOrderPage(request);
    }

    //查询订单详情
    @GetMapping("queryOrderInfo")
    public R<OrderResponse> queryOrderInfo(String orderId,Long userId) {
        if (!StringUtils.hasLength(orderId)) {
            return R.fail(R.ResultCode.PARAMS_NOT_NULL);
        }
        return orderService.queryOrderInfo(orderId, userId);
    }

    //修改订单
    @PostMapping("updateOrder")
    public R<String> updateOrder(String orderId, String receiver) {
        if (!StringUtils.hasLength(orderId) || !StringUtils.hasLength(receiver)) {
            return R.fail(R.ResultCode.PARAMS_NOT_NULL);
        }
        return orderService.updateOrder(orderId, receiver);
    }

    //删除订单
    @PostMapping("deleteOrder")
    public R<String> deleteOrder(String orderId) {
        if (!StringUtils.hasLength(orderId)) {
            return R.fail(R.ResultCode.PARAMS_NOT_NULL);
        }
        return orderService.deleteOrder(orderId);
    }

}
