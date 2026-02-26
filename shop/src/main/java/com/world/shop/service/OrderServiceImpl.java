package com.world.shop.service;

import com.world.shop.entity.Order;
import com.world.shop.event.OrderCreatedEvent;
import com.world.shop.repository.OrderRepository;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.AllArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class OrderServiceImpl implements OrderService{
    private final OrderRepository repository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final MeterRegistry registry;
    private final Timer dbSaveTimer;

    public OrderServiceImpl(OrderRepository repository, KafkaTemplate<String, Object> kafkaTemplate, MeterRegistry registry) {
        this.repository = repository;
        this.kafkaTemplate = kafkaTemplate;
        this.registry = registry;
        this.dbSaveTimer = Timer.builder("db.operation.latency")
                .description("Latency of DB save operation")
                .tag("service", "command-service")
                .tag("operation", "order.save")
                .publishPercentileHistogram()
                .publishPercentiles(0.5, 0.95, 0.99)
                .register(registry);
    }

    public Order createOrder(String customerId, Double totalAmount) {

        Order order = new Order(
                UUID.randomUUID().toString(),
                customerId,
                totalAmount,
                "CREATED"
        );

        Timer.Sample sample = Timer.start();
        try {
            repository.save(order);
        } finally {
            sample.stop(dbSaveTimer);
        }

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