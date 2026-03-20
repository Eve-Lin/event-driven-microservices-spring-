package com.order.notification.service;

import com.order.notification.entity.Notification;
import com.order.notification.event.OrderCreatedEvent;
import com.order.notification.event.OrderNotificationEvent;
import com.order.notification.repository.NotificationRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@AllArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository repository;
    private final EmailService emailService;

    public void handleEvent(OrderNotificationEvent event) {

        Notification notification = new Notification();

        notification.setOrderId(event.getOrderId());
        notification.setType("ORDER_CREATED");
        notification.setMessage(event.getMessage());
        notification.setStatus("SENT");
        notification.setTotalAmount(event.getTotalAmount());
        notification.setCustomerEmail(event.getCustomerEmail());
        notification.setCreatedAt(Instant.now());

        repository.save(notification);

        // Send email
        System.out.println("The event is: " + event.getCustomerEmail());
        String emailText = "Hello " + event.getCustomerId() +
                ", your order " + event.getOrderId() +
                " of $" + event.getTotalAmount() + " has been received!";
        emailService.sendOrderNotification(event.getCustomerEmail(), "Order Confirmation", emailText);
    }
}
