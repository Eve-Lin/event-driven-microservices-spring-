package com.read.consumer.listener;

import com.read.consumer.entity.OrderView;
import com.read.consumer.event.OrderCreatedEvent;
import com.read.consumer.repository.OrderViewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderEventListener {

    private final OrderViewRepository repository;

    @KafkaListener(topics = "orders.events")
    public void handleOrderCreated(OrderCreatedEvent event, Acknowledgment ack) {

        if (!repository.existsById(event.getOrderId())) {
            OrderView view = new OrderView();
            view.setOrderId(event.getOrderId());
            view.setCustomerId(event.getCustomerId());
            view.setTotalAmount(event.getTotalAmount());
            view.setStatus("CREATED");

            repository.save(view);
        }
        ack.acknowledge();   //commiting offset after successful DB write
    }
}