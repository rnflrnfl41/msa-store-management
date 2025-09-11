package com.example.customerservice.dto;

import com.example.dto.CustomerCoupon;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class CustomerBenefitResponse {

    private int id;

    private String name;

    private String phone;

    private int points;

    private List<CustomerCoupon> coupons;

}
