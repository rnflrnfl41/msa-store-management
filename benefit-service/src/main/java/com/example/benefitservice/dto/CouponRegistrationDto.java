package com.example.benefitservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
@AllArgsConstructor
public class CouponRegistrationDto {

    private String name;

    private int amount;

    private String type;

    private LocalDate expiryDate;

    private int customerId;

    private String customerName;

}
