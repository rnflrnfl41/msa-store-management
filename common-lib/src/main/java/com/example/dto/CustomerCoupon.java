package com.example.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CustomerCoupon {
    private String id;

    private String name;

    private int amount;

    private String type;
}
