package com.example.salesservice.service;

import com.example.exception.CommonExceptionCode;
import com.example.salesservice.dto.PaymentCreateRequest;
import com.example.salesservice.dto.PaymentResponse;
import com.example.salesservice.dto.PaymentSearchCriteria;
import com.example.salesservice.entity.Payment;
import com.example.salesservice.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final ModelMapper modelMapper;

    @Transactional
    public PaymentResponse createPayment(PaymentCreateRequest request) {
        log.info("결제 내역 생성 요청: {}", request);

        Payment payment = Payment.builder()
                .amount(request.getAmount())
                .discount(request.getDiscount())
                .paymentMethod(request.getPaymentMethod())
                .pointsUsed(request.getPointsUsed())
                .visitId(request.getVisitId())
                .build();

        Payment savedPayment = paymentRepository.save(payment);
        log.info("결제 내역 생성 완료: id={}", savedPayment.getId());

        return convertToResponse(savedPayment);
    }

    public PaymentResponse getPaymentById(Integer id) {
        log.info("결제 내역 조회 요청: id={}", id);
        Payment payment = findPaymentById(id);
        return convertToResponse(payment);
    }

    public Page<PaymentResponse> searchPayments(PaymentSearchCriteria criteria) {
        log.info("결제 내역 검색 요청: {}", criteria);

        Sort sort = Sort.by(
                "desc".equalsIgnoreCase(criteria.getSortDirection()) ? Sort.Direction.DESC : Sort.Direction.ASC,
                criteria.getSortBy()
        );

        Pageable pageable = PageRequest.of(criteria.getPage(), criteria.getSize(), sort);

        Page<Payment> payments;
        if (criteria.getVisitId() != null && criteria.getStartDate() != null && criteria.getEndDate() != null) {
            payments = paymentRepository.findByVisitIdAndDateRange(
                    criteria.getVisitId(), criteria.getStartDate(), criteria.getEndDate(), pageable);
        } else if (criteria.getVisitId() != null) {
            payments = paymentRepository.findByVisitId(criteria.getVisitId(), pageable);
        } else if (criteria.getPaymentMethod() != null) {
            payments = paymentRepository.findByPaymentMethod(criteria.getPaymentMethod(), pageable);
        } else if (criteria.getStartDate() != null && criteria.getEndDate() != null) {
            payments = paymentRepository.findByCreatedAtBetween(criteria.getStartDate(), criteria.getEndDate(), pageable);
        } else if (criteria.getMinAmount() != null && criteria.getMaxAmount() != null) {
            payments = paymentRepository.findByAmountRange(criteria.getMinAmount(), criteria.getMaxAmount(), pageable);
        } else {
            payments = paymentRepository.findAll(pageable);
        }

        return payments.map(this::convertToResponse);
    }

    public List<PaymentResponse> getPaymentsByVisitId(Integer visitId) {
        log.info("방문별 결제 내역 조회 요청: visitId={}", visitId);

        List<Payment> payments = paymentRepository.findByVisitIdOrderByCreatedAtDesc(visitId);
        return payments.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public Integer getTotalSalesByVisitAndDateRange(Integer visitId, Instant startDate, Instant endDate) {
        log.info("방문 매출 통계 요청: visitId={}, startDate={}, endDate={}", visitId, startDate, endDate);

        Integer totalSales = paymentRepository.getTotalSalesByVisitAndDateRange(visitId, startDate, endDate);
        return totalSales != null ? totalSales : 0;
    }

    public Long getPaymentCountByVisitAndDateRange(Integer visitId, Instant startDate, Instant endDate) {
        log.info("방문 결제 내역 건수 통계 요청: visitId={}, startDate={}, endDate={}", visitId, startDate, endDate);

        return paymentRepository.getPaymentCountByVisitAndDateRange(visitId, startDate, endDate);
    }

    @Transactional
    public PaymentResponse updatePayment(Integer id, PaymentCreateRequest request) {
        log.info("결제 내역 수정 요청: id={}, request={}", id, request);

        Payment payment = findPaymentById(id);

        payment.setAmount(request.getAmount());
        payment.setDiscount(request.getDiscount());
        payment.setPaymentMethod(request.getPaymentMethod());
        payment.setPointsUsed(request.getPointsUsed());
        payment.setVisitId(request.getVisitId());

        Payment updatedPayment = paymentRepository.save(payment);
        log.info("결제 내역 수정 완료: id={}", updatedPayment.getId());

        return convertToResponse(updatedPayment);
    }

    @Transactional
    public void deletePayment(Integer id) {
        log.info("결제 내역 삭제 요청: id={}", id);

        Payment payment = findPaymentById(id);
        paymentRepository.delete(payment);
        log.info("결제 내역 삭제 완료: id={}", id);
    }

    private PaymentResponse convertToResponse(Payment payment) {
        PaymentResponse response = modelMapper.map(payment, PaymentResponse.class);
        response.setFinalAmount(calculateFinalAmount(payment.getAmount(), payment.getDiscount(), payment.getPointsUsed()));
        return response;
    }

    private Payment findPaymentById(Integer id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("결제 내역을 찾을 수 없습니다: id={}", id);
                    return new RuntimeException(CommonExceptionCode.PAYMENT_NOT_FOUND.getMessage());
                });
    }

    private int calculateFinalAmount(Integer amount, Integer discount, Integer pointsUsed) {
        int finalAmount = amount != null ? amount : 0;
        finalAmount -= discount != null ? discount : 0;
        finalAmount -= pointsUsed != null ? pointsUsed : 0;
        return Math.max(finalAmount, 0); // 최종 금액은 0보다 작을 수 없음
    }
}
