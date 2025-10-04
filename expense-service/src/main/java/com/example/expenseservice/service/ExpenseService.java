package com.example.expenseservice.service;

import com.example.expenseservice.dto.ExpenseData;
import com.example.expenseservice.entity.Expense;
import com.example.expenseservice.repository.ExpenseRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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
}
