package com.example.salesservice.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
@Builder
public class UsedCouponDto {

    private UUID id;

    private String name;

    private int discountAmount;

}
