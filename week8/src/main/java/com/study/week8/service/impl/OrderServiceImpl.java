package com.study.week8.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.study.week8.dao.mapper.OrderMapper;
import com.study.week8.dao.model.Order;
import com.study.week8.service.OrderService;
import org.springframework.stereotype.Service;

@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {
}
