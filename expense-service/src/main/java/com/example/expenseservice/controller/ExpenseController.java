package com.example.expenseservice.controller;

import com.example.dto.ApiResponse;
import com.example.dto.FinancialChartDto;
import com.example.dto.FinancialSummaryResponse;
import com.example.expenseservice.dto.ExpenseData;
import com.example.expenseservice.service.ExpenseService;
import com.example.util.ResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    /**
     * 요약 데이터 조회 (오늘/이번달 지출)
     * @param date
     * @param storeIdHeader
     * @return
     */
    @GetMapping("/summary/{date}")
    public ResponseEntity<ApiResponse<FinancialSummaryResponse>> summaryExpense(
            @PathVariable LocalDate date,
            @RequestHeader(X_USER_STORE_ID) String storeIdHeader
    ) {
        Integer storeId = Integer.parseInt(storeIdHeader);
        return ResponseUtil.success(expenseService.summarySales(date, storeId));
    }

    /**
     * 차트 데이터 조회 (일별/월별 지출)
     * @param type
     * @param startDate
     * @param endDate
     * @param storeIdHeader
     * @return
     */
    @GetMapping("/chart")
    public ResponseEntity<ApiResponse<FinancialChartDto>> getChartData(
            @RequestParam String type,
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate,
            @RequestHeader(X_USER_STORE_ID) String storeIdHeader
    ) {
        Integer storeId = Integer.parseInt(storeIdHeader);
        return ResponseUtil.success(expenseService.getChartData(type,startDate,endDate,storeId));
    }

    /**
     * 지출 삭제
     * @param expenseId
     * @param storeIdHeader
     * @return
     */
    @DeleteMapping("{expenseId}")
    public ResponseEntity<ApiResponse<String>> deleteExpense(
            @PathVariable int expenseId,
            @RequestHeader(X_USER_STORE_ID) String storeIdHeader
    ) {
        Integer storeId = Integer.parseInt(storeIdHeader);
        expenseService.deleteExpense(expenseId,storeId);
        return ResponseUtil.success("매출 삭제 완료");
    }

    @PostMapping
    public ResponseEntity<ApiResponse<String>> registerExpense(
            @RequestBody ExpenseData expenseData,
            @RequestHeader(X_USER_STORE_ID) String storeIdHeader
    ) {
        Integer storeId = Integer.parseInt(storeIdHeader);
        expenseService.registerExpense(expenseData, storeId);
        return ResponseUtil.created("지출 등록이 완료되었습니다");
    }

}
