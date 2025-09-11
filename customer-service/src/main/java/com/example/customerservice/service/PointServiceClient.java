package com.example.customerservice.service;

import com.example.dto.ApiResponse;
import com.example.dto.PointServiceBenefitResponse;
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
public class PointServiceClient {

    private final WebClient pointServiceWebClient;

    /**
     * 여러 고객의 혜택 정보 일괄 조회
     */
    public List<PointServiceBenefitResponse> getCustomerBenefitListBatch(List<Integer> customerIds, int storeId) {
        return pointServiceWebClient
                .post()
                .uri("/batch")
                .header(X_USER_STORE_ID, String.valueOf(storeId))
                .bodyValue(customerIds)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ApiResponse<List<PointServiceBenefitResponse>>>() {})
                .map(ApiResponse::getData)
                .block();
    }

}
