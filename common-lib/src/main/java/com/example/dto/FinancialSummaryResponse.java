package com.example.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class FinancialSummaryResponse {

    private FinancialSummary today;

    private FinancialSummary month;

}
