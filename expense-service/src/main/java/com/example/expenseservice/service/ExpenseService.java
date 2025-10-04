package com.example.expenseservice.service;

import com.example.dto.FinancialChartData;
import com.example.dto.FinancialChartDto;
import com.example.dto.FinancialSummary;
import com.example.dto.FinancialSummaryResponse;
import com.example.expenseservice.dto.ExpenseData;
import com.example.expenseservice.entity.Expense;
import com.example.expenseservice.repository.ExpenseRepository;
import com.example.util.ChartUtil;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final ModelMapper modelMapper;

    public Page<ExpenseData> getExpenseListByDate(LocalDate date, Pageable pageable, int storeId) {
        Page<Expense> expenseList = expenseRepository.findByExpenseDateAndStoreIdOrderById(date, storeId, pageable);
        return expenseList.map(expense -> modelMapper.map(expense, ExpenseData.class));
    }

    public FinancialSummaryResponse summarySales(LocalDate date, Integer storeId) {
        int month = date.getMonthValue();

        FinancialSummary todaySummary = expenseRepository.getSummaryExpenseDate(date,storeId);
        FinancialSummary monthSummary = expenseRepository.getSummaryExpenseMonth(month,storeId);

        return FinancialSummaryResponse.builder()
                .today(todaySummary)
                .month(monthSummary)
                .build();

    }

    public FinancialChartDto getChartData(String type, LocalDate startDate, LocalDate endDate, Integer storeId) {

        if ("monthly".equals(type)) {
            List<FinancialChartData> chartDataList = expenseRepository.getMonthlyChartDataByPeriod(startDate, endDate, storeId);
            return ChartUtil.getMonthlyChartData(chartDataList, startDate, endDate);
        } else if ("daily".equals(type)) {
            List<FinancialChartData> chartDataList = expenseRepository.getDailyChartDataByPeriod(startDate, endDate, storeId);
            return ChartUtil.getDailyChartData(chartDataList, startDate, endDate);
        } else {
            throw new IllegalArgumentException("Invalid chart type: " + type);
        }

    }

    public void deleteExpense(int expenseId, Integer storeId) {
        expenseRepository.deleteByIdAndStoreId(expenseId,storeId);
    }
}
