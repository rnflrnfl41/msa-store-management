package com.example.salesservice.controller;

import com.example.dto.ApiResponse;
import com.example.salesservice.dto.*;
import com.example.salesservice.service.SalesService;
import com.example.util.ResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

import static com.example.Constant.HttpHeaderConstants.X_USER_STORE_ID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/sales")
public class SalesController {

    private final SalesService salesService;

    @GetMapping("/{customerId}/history")
    public ResponseEntity<ApiResponse<List<ServiceHistoryDto>>> getCustomerServiceHistory(
            @PathVariable Integer customerId,
            @RequestHeader(X_USER_STORE_ID) String storeIdHeader
    ) {
        Integer storeId = Integer.parseInt(storeIdHeader);
        List<ServiceHistoryDto> historyDtoList = salesService.getCustomerServiceHistory(customerId, storeId);
        return ResponseUtil.success(historyDtoList);
    }

    @PostMapping("/registration")
    public ResponseEntity<ApiResponse<String>> registerSales(
            @RequestBody SalesRegistrationDto registrationDto,
            @RequestHeader(X_USER_STORE_ID) String storeIdHeader
    ) {
        Integer storeId = Integer.parseInt(storeIdHeader);
        salesService.registerSales(registrationDto, storeId);
        return ResponseUtil.created("매출 등록이 완료되었습니다");
    }

    /**
     * 요약 데이터 조회 (오늘/이번달 매출)
     * @param date
     * @param storeIdHeader
     * @return
     */
    @GetMapping("/summary/{date}")
    public ResponseEntity<ApiResponse<SalesSummaryResponse>> summarySales(
            @PathVariable String date,
            @RequestHeader(X_USER_STORE_ID) String storeIdHeader
    ) {
        Integer storeId = Integer.parseInt(storeIdHeader);
        return ResponseUtil.success(salesService.summarySales(date, storeId));
    }

    /**
     * 차트 데이터 조회 (일별/월별 매출)
     * @param type
     * @param startDate
     * @param endDate
     * @param storeIdHeader
     * @return
     */
    @GetMapping("/chart")
    public ResponseEntity<ApiResponse<SalesChartResponse>> getChartData(
            @RequestParam String type,
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate,
            @RequestHeader(X_USER_STORE_ID) String storeIdHeader
    ) {
        Integer storeId = Integer.parseInt(storeIdHeader);
        return ResponseUtil.success(salesService.getChartData(type,startDate,endDate,storeId));
    }

    /**
     * 매출 목록 조회
     * @param date
     * @param page
     * @param limit
     * @param storeIdHeader
     * @return
     */
    @GetMapping("/chart")
    public ResponseEntity<ApiResponse<List<SalesDataDto>>> getSalesList(
            @RequestParam LocalDate date,
            @RequestParam Integer page,
            @RequestParam Integer limit,
            @RequestHeader(X_USER_STORE_ID) String storeIdHeader
    ) {
        Integer storeId = Integer.parseInt(storeIdHeader);
        return ResponseUtil.success(salesService.getSalesList(date,page,limit,storeId));
    }

}
