package com.example.customerservice.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CustomerCoupon {
    private String id;

    private String name;

    private String amount;

    private String type;
}
