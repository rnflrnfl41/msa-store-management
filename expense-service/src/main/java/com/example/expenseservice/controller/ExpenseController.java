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
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

import static com.example.Constant.HttpHeaderConstants.X_USER_STORE_ID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/expense")
public class ExpenseController {

    private final ExpenseService expenseService;

    /**
     * 날짜 기준 지출 조회
     * @param date
     * @param pageable
     * @param storeIdHeader
     * @return
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Page<ExpenseData>>> getExpenseListByDate(
            @RequestParam LocalDate date,
            @PageableDefault(size = 5) Pageable pageable,
            @RequestHeader(X_USER_STORE_ID) String storeIdHeader
    ) {
        Integer storeId = Integer.parseInt(storeIdHeader);
        return ResponseUtil.success(expenseService.getExpenseListByDate(date,pageable, storeId));
    }

}
