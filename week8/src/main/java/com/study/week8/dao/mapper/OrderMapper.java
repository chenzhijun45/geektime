package com.study.week8.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.study.week8.controller.request.OrderQueryRequest;
import com.study.week8.dao.model.Order;

import java.util.List;

public interface OrderMapper extends BaseMapper<Order> {

    List<Order> queryPage(OrderQueryRequest request);

    Integer queryCount(OrderQueryRequest request);
}
