package com.example.salesservice.controller;

import com.example.dto.ApiResponse;
import com.example.dto.FinancialSummaryResponse;
import com.example.dto.FinancialChartDto;
import com.example.salesservice.dto.*;
import com.example.salesservice.service.SalesService;
import com.example.util.ResponseUtil;
import lombok.Getter;
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

    /**
     *
     * @param customerId
     * @param storeIdHeader
     * @return
     */
    @GetMapping("/{customerId}/history")
    public ResponseEntity<ApiResponse<List<ServiceHistoryDto>>> getCustomerServiceHistory(
            @PathVariable Integer customerId,
            @RequestHeader(X_USER_STORE_ID) String storeIdHeader
    ) {
        Integer storeId = Integer.parseInt(storeIdHeader);
        List<ServiceHistoryDto> historyDtoList = salesService.getCustomerServiceHistory(customerId, storeId);
        return ResponseUtil.success(historyDtoList);
    }

    /**
     * 매출 등록
     * @param registrationDto
     * @param storeIdHeader
     * @return
     */
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
     * 오늘 매출 조회
     * @param storeIdHeader
     * @return
     */
    @GetMapping("/summary/today")
    public ResponseEntity<ApiResponse<Long>> getTodaySales(
            @RequestHeader(X_USER_STORE_ID) String storeIdHeader
    ) {
        Integer storeId = Integer.parseInt(storeIdHeader);
        return ResponseUtil.success(salesService.getTodaySales(storeId));
    }


    /**
     * 요약 데이터 조회 (오늘/이번달 매출)
     * @param date
     * @param storeIdHeader
     * @return
     */
    @GetMapping("/summary/{date}")
    public ResponseEntity<ApiResponse<FinancialSummaryResponse>> summarySales(
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
    public ResponseEntity<ApiResponse<FinancialChartDto>> getChartData(
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
    @GetMapping
    public ResponseEntity<ApiResponse<SalesDataResponse>> getSalesList(
            @RequestParam LocalDate date,
            @RequestParam Integer page,
            @RequestParam Integer limit,
            @RequestHeader(X_USER_STORE_ID) String storeIdHeader
    ) {
        Integer storeId = Integer.parseInt(storeIdHeader);
        return ResponseUtil.success(salesService.getSalesList(date,page,limit,storeId));
    }

    /**
     * 매출 삭제
     * @param visitId
     * @param storeIdHeader
     * @return
     */
    @DeleteMapping("{visitId}")
    public ResponseEntity<ApiResponse<String>> deleteSales(
            @PathVariable int visitId,
            @RequestHeader(X_USER_STORE_ID) String storeIdHeader
    ) {
        Integer storeId = Integer.parseInt(storeIdHeader);
        salesService.deleteSales(visitId,storeId);
        return ResponseUtil.success("매출 삭제 완료");
    }

}
