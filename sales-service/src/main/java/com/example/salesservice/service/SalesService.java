package com.example.salesservice.service;

import com.example.dto.BenefitUseRequest;
import com.example.exception.CommonException;
import com.example.exception.CommonExceptionCode;
import com.example.salesservice.dto.SalesRegistrationDto;
import com.example.salesservice.dto.ServiceHistoryDto;
import com.example.salesservice.dto.ServiceItemDto;
import com.example.salesservice.entity.ErrorLog;
import com.example.salesservice.entity.Payment;
import com.example.salesservice.entity.ServiceItem;
import com.example.salesservice.entity.Visit;
import com.example.salesservice.repository.ErrorLogRepository;
import com.example.salesservice.repository.PaymentRepository;
import com.example.salesservice.repository.ServiceItemRepository;
import com.example.salesservice.repository.VisitRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.example.Constant.BenefitConstant.BENEFIT_USE_COMPLETE;
import static com.example.Constant.ServiceConstants.INTERNAL_SERVICE_ERROR_CODE;

@Service
@RequiredArgsConstructor
@Slf4j
public class SalesService {

    private final PaymentRepository paymentRepository;
    private final VisitRepository visitRepository;
    private final ServiceItemRepository serviceItemRepository;
    private final ModelMapper modelMapper;
    private final BenefitServiceClient benefitServiceClient;
    private final ErrorLogRepository errorLogRepository;
    private final RegisterSalesService registerSalesService;

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

        //benefit-service에 보낼 parameter
        BenefitUseRequest useRequest = BenefitUseRequest.builder()
                .usedPoint(registrationDto.getUsedPoint())
                .usedCouponId(registrationDto.getUsedCouponId())
                .customerId(registrationDto.getCustomerId())
                .build();

        boolean benefitUsed = false;

        try{

            //benefit-service에 api 보내는 작업
            String response = benefitServiceClient.usePointCoupon(useRequest,storeId);

            if(!response.equals(BENEFIT_USE_COMPLETE)){
                throw new CommonException(CommonExceptionCode.BENEFIT_USE_FAILED);
            }

            benefitUsed = true;

            //별도 트랜잭션을 사용 해야하는대 같은 클래스 내의 메서드를 참조할때는 @Transcational이 안되서 별도 서비스로 분리
            registerSalesService.saveSalesData(registrationDto,storeId);

        }catch (Exception e){

            if (benefitUsed) {
                try {
                    String rollbackResponse = benefitServiceClient.usePointCouponRollback(useRequest, storeId);
                    log.info("혜택 사용 롤백 완료: {}", rollbackResponse);
                } catch (Exception rollbackException) {

                    log.error("🚨 혜택 사용 롤백 실패 - 데이터 불일치 발생!", rollbackException);
                    log.error("고객 ID: {}, 상점 ID: {}, 사용된 포인트: {}, 사용된 쿠폰: {}", 
                            registrationDto.getCustomerId(), storeId, 
                            registrationDto.getUsedPoint(), registrationDto.getUsedCouponId());

                    errorLogRepository.save(ErrorLog.builder()
                            .uri("benefit-service usePointCouponRollback fail")
                            .code(INTERNAL_SERVICE_ERROR_CODE)
                            .errorId(UUID.randomUUID().toString())
                            .message(rollbackException.getMessage())
                            .status(500)
                            .stackTrace(ExceptionUtils.getStackTrace(rollbackException))
                            .build()
                    );

                }
            }

            throw e;

        }

    }


}
