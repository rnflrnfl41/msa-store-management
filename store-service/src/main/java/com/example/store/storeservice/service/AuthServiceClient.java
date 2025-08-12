package com.example.store.storeservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RequiredArgsConstructor
public class AuthServiceClient {

    private final WebClient.Builder webClientBuilder;

    public ResponseEntity<Void> deleteUserByStoreId(int storeId, String internalToken, String userId, String userRole) {
        return webClientBuilder
                 .build()
                .delete()
                .uri("http://auth-service/storeId/" + storeId)
                .header("X-INTERNAL-TOKEN", internalToken)
                .header("X-USER-ROLE", userRole)
                .retrieve()
                .toBodilessEntity()
                .block(); // 동기 방식. 필요 시 비동기로 처리 가능
    }


}
