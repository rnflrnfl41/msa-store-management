package com.example.expenseservice.repository;


import com.example.dto.FinancialChartData;
import com.example.dto.FinancialSummary;
import com.example.expenseservice.dto.ExpenseData;
import com.example.expenseservice.entity.Expense;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

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

    // 월별 차트 데이터
    @Query("select new com.example.dto.FinancialChartData(" +
            "e.expenseDate, " +
            "COALESCE(sum(e.amount),0), " +
            "count(e)" +
            ")" +
            " from Expense e where e.storeId = :storeId and " +
            " e.expenseDate between :startDate and :endDate " +
            " group by year(e.expenseDate), month(e.expenseDate) " +
            " order by year(e.expenseDate), month(e.expenseDate)")
    List<FinancialChartData> getMonthlyChartDataByPeriod(LocalDate startDate, LocalDate endDate, int storeId);

    // 일별 차트 데이터
    @Query("select new com.example.dto.FinancialChartData(" +
            "e.expenseDate, " +
            "COALESCE(sum(e.amount),0), " +
            "count(e)" +
            ")" +
            " from Expense e where e.storeId = :storeId and " +
            " e.expenseDate between :startDate and :endDate " +
            " group by e.expenseDate " +
            " order by e.expenseDate")
    List<FinancialChartData> getDailyChartDataByPeriod(LocalDate startDate, LocalDate endDate, int storeId);
}
