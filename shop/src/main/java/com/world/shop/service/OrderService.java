package com.world.shop.service;

import com.world.shop.entity.Order;

public interface OrderService {
    public Order createOrderAndNotify(String customerId, Double totalAmount, String customerEmail, String key);
}
