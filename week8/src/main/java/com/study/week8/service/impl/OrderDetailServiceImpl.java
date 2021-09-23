package com.study.week8.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.study.week8.dao.mapper.OrderDetailMapper;
import com.study.week8.dao.model.OrderDetail;
import com.study.week8.service.OrderDetailService;
import org.springframework.stereotype.Service;

@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail> implements OrderDetailService {
}
