package com.example.salesservice.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class Pagination {

    private int page;

    private int total;

    private int totalPages;

}
