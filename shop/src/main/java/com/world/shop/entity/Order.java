package com.world.shop.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String customerId;
    private Double totalAmount;
    private String customerEmail;
    private String status;
    @Column(unique = true)
    private String idempotencyKey;
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    public Order(String customerId, Double totalAmount, String customerEmail, String status, String idempotencyKey) {
        this.customerId = customerId;
        this.totalAmount = totalAmount;
        this.customerEmail = customerEmail;
        this.status = status;
        this.idempotencyKey = idempotencyKey;
    }
}