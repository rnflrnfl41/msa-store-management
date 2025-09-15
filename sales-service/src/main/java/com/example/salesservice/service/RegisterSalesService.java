package com.example.salesservice.service;

import com.example.exception.CommonException;
import com.example.salesservice.dto.SalesRegistrationDto;
import com.example.salesservice.entity.Payment;
import com.example.salesservice.entity.ServiceItem;
import com.example.salesservice.entity.Visit;
import com.example.salesservice.repository.PaymentRepository;
import com.example.salesservice.repository.ServiceItemRepository;
import com.example.salesservice.repository.VisitRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Service
@RequiredArgsConstructor
@Slf4j
public class RegisterSalesService {

    private final PaymentRepository paymentRepository;
    private final VisitRepository visitRepository;
    private final ServiceItemRepository serviceItemRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveSalesData(SalesRegistrationDto registrationDto, Integer storeId) {

        log.info("=== 트랜잭션 시작 ===");
        log.info("현재 트랜잭션 상태: {}", TransactionSynchronizationManager.isActualTransactionActive());

        Visit visit = visitRepository.save(
                Visit.builder()
                        .customerId(registrationDto.getCustomerId())
                        .visitDate(registrationDto.getVisitDate())
                        .totalServiceAmount(registrationDto.getTotalServiceAmount())
                        .finalServiceAmount(registrationDto.getFinalServiceAmount())
                        .memo(registrationDto.getMemo())
                        .storeId(storeId)
                        .build());

        log.info("Visit 저장 완료: {}", visit.getId());
        log.info("=== 예외 발생 전 ===");

        paymentRepository.save(Payment.builder()
                .amount(registrationDto.getFinalServiceAmount())
                .discount(registrationDto.getDiscountAmount())
                .paymentMethod(registrationDto.getPaymentMethod())
                .pointsUsed(registrationDto.getUsedPoint())
                .visit(visit)
                .build()
        );

        registrationDto.getServiceList().stream()
                .map(s -> ServiceItem.builder()
                        .serviceName(s.getName())
                        .price(s.getPrice())
                        .visit(visit)
                        .build())
                .forEach(serviceItemRepository::save);
    }

}
