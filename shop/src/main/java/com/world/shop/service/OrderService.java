package com.world.shop.service;

import com.world.shop.entity.Order;

public interface OrderService {
    public Order createOrder(String customerId, Double totalAmount);
}
