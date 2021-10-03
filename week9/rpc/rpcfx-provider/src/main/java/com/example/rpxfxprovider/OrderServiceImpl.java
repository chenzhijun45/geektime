package com.example.rpxfxprovider;

import com.example.rpcfxapi.Order;
import com.example.rpcfxapi.OrderService;

public class OrderServiceImpl implements OrderService {

    @Override
    public Order findOrderById(int id) {
        return new Order(1, "czj", 1.23f);
    }

}
