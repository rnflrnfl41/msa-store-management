package com.example.expenseservice.repository;


import com.example.dto.FinancialSummary;
import com.example.expenseservice.dto.ExpenseData;
import com.example.expenseservice.entity.Expense;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;

public interface ExpenseRepository extends JpaRepository <Expense, Integer> {

    Page<Expense> findByExpenseDateAndStoreIdOrderById(LocalDate expenseDate, int storeId, Pageable pageable);

    @Query("select new com.example.dto.FinancialSummary(" +
            "COALESCE(sum(e.amount),0),count(e) " +
            ")" +
            "from Expense e where e.expenseDate = :date and e.storeId = :storeId")
    FinancialSummary getSummaryExpenseDate(LocalDate date, int storeId);

    @Query("select new com.example.dto.FinancialSummary(" +
            "COALESCE(sum(e.amount),0), count(e)" +
            ")" +
            " from Expense e where month(e.expenseDate) = :month and e.storeId = :storeId ")
    FinancialSummary getSummaryExpenseMonth(int month, int storeId);

}
