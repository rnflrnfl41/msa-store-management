package com.example.customerservice.dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CustomerSearchCriteria {
    
    // 통합 검색어 (이름, 전화번호에서 검색)
    private String keyword;
    
    // 개별 필드 검색
    private String name;
    private String phone;
    
    // 날짜 범위 검색
    private LocalDate startDate;
    private LocalDate endDate;
    
    // 정렬
    private String sortBy = "createdAt";
    private String sortOrder = "desc";
    
    // 페이지네이션
    private int page = 0;
    private int size = 10;
} 