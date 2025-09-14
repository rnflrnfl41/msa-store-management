package com.example.benefitservice.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@Builder
public class BenefitResponse {

    private int points;

    private List<CouponDto> couponDtoList;

}
