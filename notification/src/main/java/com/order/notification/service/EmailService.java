package com.order.notification.service;

public interface EmailService {

    public void sendOrderNotification(String to, String subject, String text);
}
