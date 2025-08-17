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
public class PaymentStatisticsResponse {

    private Integer visitId;
    private Instant startDate;
    private Instant endDate;
    private Integer totalSales;
    private Long paymentCount;
    private Double averageAmount;
} 