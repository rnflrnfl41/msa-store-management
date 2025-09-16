package com.example.salesservice.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class SalesSummary {

    private int amount;

    private int count;

}
