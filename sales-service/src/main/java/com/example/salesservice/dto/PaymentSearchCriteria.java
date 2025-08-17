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
public class PaymentSearchCriteria {

    private String paymentMethod;
    private Integer visitId;
    private Instant startDate;
    private Instant endDate;
    private Integer minAmount;
    private Integer maxAmount;
    @Builder.Default
    private String sortBy = "createdAt";
    @Builder.Default
    private String sortDirection = "desc";
    @Builder.Default
    private Integer page = 0;
    @Builder.Default
    private Integer size = 10;
} 