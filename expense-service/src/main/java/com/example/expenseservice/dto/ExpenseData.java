package com.example.expenseservice.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
public class ExpenseData {

    private int id;

    private int amount;

    private String categoryName;

    private String memo;

    private Date expenseDate;

}
