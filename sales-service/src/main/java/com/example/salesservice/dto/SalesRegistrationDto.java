package com.example.salesservice.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Setter
@Getter
public class SalesRegistrationDto {

    private int customerId;

    private LocalDate visitDate;

    private int totalServiceAmount;

    private int discountAmount;

    private int finalServiceAmount;

    private String memo;

    private String paymentMethod;

    private String usedCouponId;

    private int usedPoint;

    private List<ServiceItemDto> serviceList;

}
