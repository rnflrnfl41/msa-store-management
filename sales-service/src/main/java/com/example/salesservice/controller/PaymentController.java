package com.example.salesservice.controller;

import com.example.salesservice.dto.PaymentCreateRequest;
import com.example.salesservice.dto.PaymentResponse;
import com.example.salesservice.dto.PaymentSearchCriteria;
import com.example.salesservice.dto.PaymentSearchRequest;
import com.example.salesservice.dto.PaymentStatisticsRequest;
import com.example.salesservice.dto.PaymentStatisticsResponse;
import com.example.salesservice.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Payment", description = "결제 내역 관리 API")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    @Operation(summary = "결제 내역 생성", description = "새로운 결제 내역을 생성합니다.")
    public ResponseEntity<PaymentResponse> createPayment(@Valid @RequestBody PaymentCreateRequest request) {
        log.info("결제 내역 생성 API 호출: {}", request);
        PaymentResponse response = paymentService.createPayment(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "결제 내역 조회", description = "id로 결제 내역 정보를 조회합니다.")
    public ResponseEntity<PaymentResponse> getPayment(@PathVariable Integer id) {
        log.info("결제 내역 조회 API 호출: id={}", id);
        PaymentResponse response = paymentService.getPaymentById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "결제 내역 검색", description = "다양한 조건으로 결제 내역을 검색합니다.")
    public ResponseEntity<Page<PaymentResponse>> searchPayments(PaymentSearchRequest request) {

        log.info("결제 내역 검색 API 호출: {}", request);

        PaymentSearchCriteria criteria = PaymentSearchCriteria.builder()
                .paymentMethod(request.getPaymentMethod())
                .visitId(request.getVisitId())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .minAmount(request.getMinAmount())
                .maxAmount(request.getMaxAmount())
                .sortBy(request.getSortBy())
                .sortDirection(request.getSortDirection())
                .page(request.getPage())
                .size(request.getSize())
                .build();

        Page<PaymentResponse> response = paymentService.searchPayments(criteria);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/visit/{visitId}")
    @Operation(summary = "방문별 결제 내역 조회", description = "특정 방문의 모든 결제 내역을 조회합니다.")
    public ResponseEntity<List<PaymentResponse>> getPaymentsByVisit(@PathVariable Integer visitId) {
        log.info("방문별 결제 내역 조회 API 호출: visitId={}", visitId);
        List<PaymentResponse> response = paymentService.getPaymentsByVisitId(visitId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/visit/{visitId}/statistics")
    @Operation(summary = "방문 매출 통계", description = "특정 방문의 매출 통계를 조회합니다.")
    public ResponseEntity<PaymentStatisticsResponse> getVisitStatistics(
            @PathVariable Integer visitId,
            PaymentStatisticsRequest request) {

        log.info("방문 매출 통계 API 호출: visitId={}, request={}", visitId, request);

        Integer totalSales = paymentService.getTotalSalesByVisitAndDateRange(visitId, request.getStartDate(), request.getEndDate());
        Long paymentCount = paymentService.getPaymentCountByVisitAndDateRange(visitId, request.getStartDate(), request.getEndDate());

        PaymentStatisticsResponse statistics = PaymentStatisticsResponse.builder()
                .visitId(visitId)
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .totalSales(totalSales)
                .paymentCount(paymentCount)
                .averageAmount(paymentCount > 0 ? (double) totalSales / paymentCount : 0.0)
                .build();
        
        return ResponseEntity.ok(statistics);
    }

    @PutMapping("/{id}")
    @Operation(summary = "결제 내역 수정", description = "기존 결제 내역 정보를 수정합니다.")
    public ResponseEntity<PaymentResponse> updatePayment(
            @PathVariable Integer id,
            @Valid @RequestBody PaymentCreateRequest request) {

        log.info("결제 내역 수정 API 호출: id={}, request={}", id, request);
        PaymentResponse response = paymentService.updatePayment(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "결제 내역 삭제", description = "결제 내역을 삭제합니다.")
    public ResponseEntity<Void> deletePayment(@PathVariable Integer id) {
        log.info("결제 내역 삭제 API 호출: id={}", id);
        paymentService.deletePayment(id);
        return ResponseEntity.noContent().build();
    }
}
