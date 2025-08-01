package com.example.store.storeservice.controller;

import com.example.dto.ApiResponse;
import com.example.store.storeservice.dto.StoreCreateRequest;
import com.example.store.storeservice.service.StoreService;
import com.example.util.ResponseUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/store")
@RequiredArgsConstructor
public class StoreController {

    private final StoreService storeService;

    @PostMapping
    public ResponseEntity<ApiResponse<String>> createStore(@Valid @RequestBody StoreCreateRequest request) {
        storeService.createStore(request);
        return ResponseUtil.created("매장 생성 완료");
    }

}
