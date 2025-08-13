package com.example.store.storeservice.service;


import com.example.dto.UserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import com.example.dto.ApiResponse;

import java.util.List;
import java.util.UUID;


import static com.example.Constant.HttpHeaderConstants.X_USER_ROLE;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthServiceClient {

    private final WebClient userServiceWebClient;

    public List<UserDto> getAllUserInfoByStoreId(UUID storeId, String userRole) {

        return userServiceWebClient
                .get()
                .uri("/{storeId}", storeId)
                .header(X_USER_ROLE, userRole)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ApiResponse<List<UserDto>>>() {})
                .map(ApiResponse::getData)
                .block();
    }

    public ResponseEntity<Void> deleteUserByStoreId(int storeId, String internalToken, String userId, String userRole) {
        return userServiceWebClient
                .delete()
                .uri("/store/{storeId}" + storeId)
                .header("X-USER-ROLE", userRole)
                .retrieve()
                .toBodilessEntity()
                .block(); // 동기 방식. 필요 시 비동기로 처리 가능
    }



}
