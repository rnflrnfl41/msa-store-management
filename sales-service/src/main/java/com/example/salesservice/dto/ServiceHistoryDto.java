package com.example.salesservice.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Setter
@Getter
@Builder
public class ServiceHistoryDto {

    private int historyId;
    private LocalDate date;
    private List<ServiceItemDto> services;
    private int subtotalAmount;
    private int discountAmount;
    private int finalAmount;
    private String memo;

}
