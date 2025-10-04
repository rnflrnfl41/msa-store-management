package com.example.expenseservice.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
public class ExpenseData {

    private int id;

    private int amount;

    private String categoryName;

    private String memo;

    private LocalDate expenseDate;

}
