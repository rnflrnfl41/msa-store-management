package com.example.salesservice.service;

import com.example.dto.ApiResponse;
import com.example.dto.BenefitServiceBenefitResponse;
import com.example.dto.BenefitUseRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

import static com.example.Constant.HttpHeaderConstants.X_USER_STORE_ID;

@Component
@RequiredArgsConstructor
@Slf4j
public class BenefitServiceClient {

    private final WebClient benefitServiceWebClient;

    /**
     * 포인트 및 쿠폰 사용
     */
    public String usePointCoupon(BenefitUseRequest request, int storeId) {
        return benefitServiceWebClient
                .post()
                .uri("/use")
                .header(X_USER_STORE_ID, String.valueOf(storeId))
                .bodyValue(request)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ApiResponse<String>>() {})
                .map(ApiResponse::getData)
                .block();
    }

    /**
     * 포인트 및 쿠폰 사용 롤백
     */
    public String usePointCouponRollback(BenefitUseRequest request, int storeId) {
        return benefitServiceWebClient
                .post()
                .uri("/use/rollback")
                .header(X_USER_STORE_ID, String.valueOf(storeId))
                .bodyValue(request)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ApiResponse<String>>() {})
                .map(ApiResponse::getData)
                .block();
    }

}
