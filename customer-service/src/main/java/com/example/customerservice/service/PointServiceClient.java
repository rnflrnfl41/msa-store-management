package com.example.customerservice.service;

import com.example.dto.ApiResponse;
import com.example.dto.UserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class PointServiceClient {

    private final WebClient pointServiceWebClient;

    public List<UserDto> getAllUserInfoByStoreId(int storeId) {

        return pointServiceWebClient
                .post()
                .uri("/batch")
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ApiResponse<List<UserDto>>>() {})
                .map(ApiResponse::getData)
                .block();
    }

}
