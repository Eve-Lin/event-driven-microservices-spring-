package com.read.consumer.listener;

import com.read.consumer.entity.OrderView;
import com.read.consumer.event.OrderCreatedEvent;
import com.read.consumer.repository.OrderViewRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
public class OrderEventListener {

    private final OrderViewRepository repository;
    private final MeterRegistry meterRegistry;
    private final Timer dbSaveTimer;

    public OrderEventListener(OrderViewRepository repository, MeterRegistry meterRegistry) {
        this.repository = repository;
        this.meterRegistry = meterRegistry;
        this.dbSaveTimer = Timer.builder("db.operation.latency")
                .description("Latency of DB save operation")
                .tag("operation", "orderView.save")
                .publishPercentileHistogram()
                .publishPercentiles(0.5, 0.95, 0.99)
                .register(meterRegistry);
    }

    @KafkaListener(topics = "orders.events")
    @CircuitBreaker(name = "orderViewBreaker")
    public void handleOrderCreated(OrderCreatedEvent event, Acknowledgment ack) {
        if (!repository.existsById(event.getOrderId())) {
            OrderView view = new OrderView();
            view.setOrderId(event.getOrderId());
            view.setCustomerId(event.getCustomerId());
            view.setTotalAmount(event.getTotalAmount());
            view.setStatus("CREATED");

            Timer.Sample sample = Timer.start();
            try {
                repository.save(view);
            } finally {
                sample.stop(dbSaveTimer);
            }
        }
        ack.acknowledge();   //commiting offset after successful DB write
    }
}