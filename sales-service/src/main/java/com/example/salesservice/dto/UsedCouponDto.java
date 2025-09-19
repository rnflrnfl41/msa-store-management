package com.example.salesservice.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UsedCouponDto {

    private String id;

    private int name;

    private int discountAmount;

}
