package com.example.store.storeservice.service;


import com.example.dto.UserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import com.example.dto.ApiResponse;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthServiceClient {

    private final WebClient userServiceWebClient;

    public List<UserDto> getAllUserInfoByStoreId(int storeId) {

        return userServiceWebClient
                .get()
                .uri("/{storeId}", storeId)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ApiResponse<List<UserDto>>>() {})
                .map(ApiResponse::getData)
                .block();
    }

    public String deleteUserByStoreId(int storeId) {
        return userServiceWebClient
                .delete()
                .uri("/store/{storeId}", storeId)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ApiResponse<String>>() {})
                .map(ApiResponse::getData)
                .block();
    }



}
