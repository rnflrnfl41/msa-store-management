package com.example.customerservice.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CustomerBenefitResponse {

    private int customerId;

    private String name;

    private String phone;

    private int points;

    private CustomerCoupon coupons;

}
