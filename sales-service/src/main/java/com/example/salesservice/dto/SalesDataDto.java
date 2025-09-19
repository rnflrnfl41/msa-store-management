package com.example.salesservice.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class SalesDataDto {

    private int id;

    private int originalAmount;

    private int discountAmount;

    private int finalAmount;

    private String memo;

    private String date;

    private String time;

    private String paymentMethod;

    private String customerName;

    private UsedCouponDto usedCoupon;

    private int usedPoints;

}
