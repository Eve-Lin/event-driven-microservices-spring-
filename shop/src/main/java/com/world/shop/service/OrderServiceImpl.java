package com.world.shop.service;

import com.world.shop.entity.Order;
import com.world.shop.event.OrderCreatedEvent;
import com.world.shop.repository.OrderRepository;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Service
public class OrderServiceImpl implements OrderService {
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

    public Order createOrderAndNotify(String customerId, Double totalAmount, String customerEmail, String key) {


        Order order = createOrder(customerId,totalAmount, customerEmail, key);

            OrderCreatedEvent event = new OrderCreatedEvent(
                    UUID.randomUUID().toString(),
                    Objects.nonNull(order.getId()) ? order.getId().toString() : null,
                    order.getCustomerId(),
                    order.getTotalAmount(),
                    order.getCustomerEmail(),
                    order.getCreatedAt(),
                    key
            );
            kafkaTemplate.send("orders.events", order.getId().toString(), event);
        return order;
    }

    @Transactional
    public Order createOrder(String customerId, Double totalAmount, String customerEmail, String key) {
        Order order = new Order(
                customerId,
                totalAmount,
                customerEmail,
                "CREATED",
                key
        );
        Timer.Sample sample = Timer.start();
        try {
            order = repository.save(order);
        } catch (DataIntegrityViolationException e) {
            return repository.findByIdempotencyKey(key)
                    .orElseThrow(() ->
                            new IllegalStateException("Order exists but could not be retrieved"));
        } finally {
            sample.stop(dbSaveTimer);
        }

        return order;
    }
}