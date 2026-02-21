package com.read.consumer.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;


@Entity
@Data
public class OrderView {

    @Id
    private String orderId;

    private String customerId;
    private Double totalAmount;
    private String status;
}