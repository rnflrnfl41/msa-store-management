package com.example.pointservice.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@Builder
public class CustomerBenefitResponse {

    private int customerId;

    private int points;

    private List<CouponDto> couponDtoList;

}
