package com.example.expenseservice.repository;


import com.example.expenseservice.dto.Expense;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExpenseRepository extends JpaRepository <Expense, Integer> {
}
