package com.example.salesservice.service;

import com.example.salesservice.dto.SalesRegistrationDto;
import com.example.salesservice.entity.Payment;
import com.example.salesservice.entity.ServiceItem;
import com.example.salesservice.entity.Visit;
import com.example.salesservice.repository.PaymentRepository;
import com.example.salesservice.repository.ServiceItemRepository;
import com.example.salesservice.repository.VisitRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class RegisterSalesService {

    private final PaymentRepository paymentRepository;
    private final VisitRepository visitRepository;
    private final ServiceItemRepository serviceItemRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveSalesData(SalesRegistrationDto registrationDto, Integer storeId) {

        Visit visit = visitRepository.save(
                Visit.builder()
                        .customerId(registrationDto.getCustomerId())
                        .customerName(registrationDto.getCustomerName())
                        .visitDate(registrationDto.getVisitDate())
                        .visitTime(registrationDto.getVisitTime())
                        .totalServiceAmount(registrationDto.getTotalServiceAmount())
                        .finalServiceAmount(registrationDto.getFinalServiceAmount())
                        .memo(registrationDto.getMemo())
                        .storeId(storeId)
                        .build());

        Payment payment = Payment.builder()
                .amount(registrationDto.getFinalServiceAmount())
                .discount(registrationDto.getDiscountAmount())
                .paymentMethod(registrationDto.getPaymentMethod())
                .pointsUsed(registrationDto.getUsedPoint())
                .visit(visit)
                .build();

        if(registrationDto.getUsedCouponId() != null && !registrationDto.getUsedCouponId().isEmpty()){
            payment.setUsedCouponId(UUID.fromString(registrationDto.getUsedCouponId()));
            payment.setUsedCouponName(registrationDto.getUsedCouponName());
        }

        paymentRepository.save(payment);

        registrationDto.getServiceList().stream()
                .map(s -> ServiceItem.builder()
                        .serviceName(s.getName())
                        .price(s.getPrice())
                        .visit(visit)
                        .build())
                .forEach(serviceItemRepository::save);
    }

}
