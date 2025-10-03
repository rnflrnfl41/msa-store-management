package com.example.expenseservice.repository;


import com.example.expenseservice.dto.ExpenseData;
import com.example.expenseservice.entity.Expense;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface ExpenseRepository extends JpaRepository <Expense, Integer> {

    Page<Expense> findByExpenseDateOrderById(LocalDate expenseDate, Pageable pageable);

}
