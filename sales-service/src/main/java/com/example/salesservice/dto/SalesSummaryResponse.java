package com.example.salesservice.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class SalesSummaryResponse {

    private SalesSummary today;

    private SalesSummary month;

}
