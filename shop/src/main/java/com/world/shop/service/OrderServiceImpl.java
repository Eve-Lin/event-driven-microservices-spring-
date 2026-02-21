package com.world.shop.service;

import com.world.shop.entity.Order;
import com.world.shop.event.OrderCreatedEvent;
import com.world.shop.repository.OrderRepository;
import lombok.AllArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@AllArgsConstructor
public class OrderServiceImpl implements OrderService{
    private final OrderRepository repository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public Order createOrder(String customerId, Double totalAmount) {

        Order order = new Order(
                UUID.randomUUID().toString(),
                customerId,
                totalAmount,
                "CREATED"
        );

        repository.save(order);

        OrderCreatedEvent event = new OrderCreatedEvent(
                order.getId(),
                order.getCustomerId(),
                order.getTotalAmount(),
                Instant.now()
        );

        kafkaTemplate.send("orders.events", order.getId(), event);

        return order;
    }
}