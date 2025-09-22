package com.example.salesservice.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Setter
@Getter
public class SalesRegistrationDto {

    private int customerId;

    private String customerName;

    private LocalDate visitDate;

    private LocalTime visitTime;

    private int totalServiceAmount;

    private int discountAmount;

    private int finalServiceAmount;

    private String memo;

    private String paymentMethod;

    private String usedCouponId;

    private String usedCouponName;

    private int usedPoint;

    private List<ServiceItemDto> serviceList;

}
