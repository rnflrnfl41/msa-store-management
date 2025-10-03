package com.example.expenseservice.controller;

import com.example.dto.ApiResponse;
import com.example.expenseservice.dto.ExpenseData;
import com.example.expenseservice.service.ExpenseService;
import com.example.util.ResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/expense")
public class ExpenseController {

    private final ExpenseService expenseService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<ExpenseData>>> getExpenseListByDate(
            @RequestParam LocalDate date,
            @PageableDefault(size = 5) Pageable pageable
    ) {
        return ResponseUtil.success(expenseService.getExpenseListByDate(date,pageable));
    }

}
