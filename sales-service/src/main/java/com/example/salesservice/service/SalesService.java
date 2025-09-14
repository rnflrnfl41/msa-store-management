package com.example.salesservice.service;

import com.example.dto.BenefitUseRequest;
import com.example.exception.CommonException;
import com.example.exception.CommonExceptionCode;
import com.example.salesservice.dto.SalesRegistrationDto;
import com.example.salesservice.dto.ServiceHistoryDto;
import com.example.salesservice.dto.ServiceItemDto;
import com.example.salesservice.entity.Payment;
import com.example.salesservice.entity.ServiceItem;
import com.example.salesservice.entity.Visit;
import com.example.salesservice.repository.PaymentRepository;
import com.example.salesservice.repository.ServiceItemRepository;
import com.example.salesservice.repository.VisitRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.example.Constant.BenefitConstant.BENEFIT_USE_COMPLETE;

@Service
@RequiredArgsConstructor
@Slf4j
public class SalesService {

    private final PaymentRepository paymentRepository;
    private final VisitRepository visitRepository;
    private final ServiceItemRepository serviceItemRepository;
    private final ModelMapper modelMapper;
    private final BenefitServiceClient benefitServiceClient;

    public List<ServiceHistoryDto> getCustomerServiceHistory(Integer customerId, Integer storeId) {
        return visitRepository.findByStoreIdAndCustomerId(storeId, customerId)
                .stream()
                .map(v -> {
                    List<ServiceItemDto> serviceItemDtos = v.getServiceItems()
                            .stream()
                            .map(si -> modelMapper.map(si, ServiceItemDto.class))
                            .toList();

                    return ServiceHistoryDto.builder()
                            .historyId(v.getId())
                            .date(v.getVisitDate())
                            .subtotalAmount(v.getTotalServiceAmount())
                            .discountAmount(v.getTotalServiceAmount() - v.getFinalServiceAmount())
                            .finalAmount(v.getFinalServiceAmount())
                            .memo(v.getMemo())
                            .services(serviceItemDtos)
                            .build();
                })
                .toList();
    }

    public void registerSales(SalesRegistrationDto registrationDto, Integer storeId) {

        BenefitUseRequest useRequest = BenefitUseRequest.builder()
                .usedPoint(registrationDto.getUsedPoint())
                .usedCouponId(registrationDto.getUsedCouponId())
                .customerId(registrationDto.getCustomerId())
                .build();

        boolean benefitUsed = false;

        try{

            String response = benefitServiceClient.usePointCoupon(useRequest,storeId);

            if(!response.equals(BENEFIT_USE_COMPLETE)){
                throw new CommonException(CommonExceptionCode.BENEFIT_USE_FAILED);
            }

            benefitUsed = true;

            saveSalesData(registrationDto,storeId);

        }catch (Exception e){

            if (benefitUsed) {
                try {
                    String rollbackResponse = benefitServiceClient.usePointCouponRollback(useRequest, storeId);
                    log.info("혜택 사용 롤백 완료: {}", rollbackResponse);
                } catch (Exception rollbackException) {
                    log.error("혜택 사용 롤백 실패: {}", rollbackException.getMessage());
                    // 심각한 오류 - 관리자 알림 필요
                }
            }

            if (e instanceof CommonException) {
                throw e; // 이미 CommonException이면 그대로
            } else {
                throw new CommonException(HttpStatus.INTERNAL_SERVER_ERROR, "SALES_REGISTRATION_FAILED", e.getMessage());
            }

        }

    }

    @Transactional
    public void saveSalesData(SalesRegistrationDto registrationDto, Integer storeId) {
        Visit visit = modelMapper.map(registrationDto, Visit.class);
        visit.setStoreId(storeId);
        visitRepository.save(visit);

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
