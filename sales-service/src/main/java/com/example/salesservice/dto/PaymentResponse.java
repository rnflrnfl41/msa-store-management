package com.example.salesservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {

    private Integer id;
    private Integer amount;
    private Integer discount;
    private String paymentMethod;
    private Integer pointsUsed;
    private Instant createdAt;
    private Integer visitId;
    private Integer finalAmount; // 최종 결제 금액 (amount - discount - pointsUsed)
} 