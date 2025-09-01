package com.example.customerservice.dto;

import com.example.customerservice.entity.Customer;
import lombok.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CustomerResponse {
    private int id;
    private String name;
    private String phone;
    private LocalDate lastVisit;

} 