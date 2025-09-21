package com.example.salesservice.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Builder
@Setter
@Getter
public class SalesDataResponse {

    List<SalesDataDto> sales;

    Pagination pagination;
}
