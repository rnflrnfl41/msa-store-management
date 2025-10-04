package com.example.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Setter
@Getter
@Builder
public class FinancialChartDto {

    private List<Long> data;

    private List<LocalDate> dates;
    
    private List<Long> counts;

}
