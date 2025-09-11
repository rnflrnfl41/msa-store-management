package com.example.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@Builder
public class PointServiceBenefitResponse {

    private int customerId;

    private int points;

    private List<CustomerCoupon> coupons;

}
