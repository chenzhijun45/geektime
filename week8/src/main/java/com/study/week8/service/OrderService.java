package com.study.week8.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.study.week8.controller.request.OrderQueryRequest;
import com.study.week8.controller.response.OrderResponse;
import com.study.week8.controller.response.PageResult;
import com.study.week8.controller.response.R;
import com.study.week8.dao.model.Order;


public interface OrderService extends IService<Order> {

    //模拟创建订单
    R<OrderResponse> createOrder();

    //查询订单详情
    R<OrderResponse> queryOrderInfo(String orderId, Long userId);

    //分页查询订单
    R<PageResult<OrderResponse>> queryOrderPage(OrderQueryRequest request);

    //修改订单信息
    R<String> updateOrder(String orderId, String receiver);

    //删除订单
    R<String> deleteOrder(String orderId);
}
