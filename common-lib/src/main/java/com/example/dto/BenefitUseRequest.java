package com.example.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class BenefitUseRequest {

    private int customerId;

    private int usedPoint;

    private String usedCouponId;

}
