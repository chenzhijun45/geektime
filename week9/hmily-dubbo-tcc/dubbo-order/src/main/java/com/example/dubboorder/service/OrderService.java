package com.example.dubboorder.service;

import com.example.dubbocommon.order.dto.OrderDTO;


public interface OrderService {

    boolean confirm(OrderDTO orderDTO);

    boolean confirmException(OrderDTO orderDTO);
}
