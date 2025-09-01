package com.example.store.storeservice.controller;

import com.example.dto.ApiResponse;
import com.example.store.storeservice.dto.StoreCreateRequest;
import com.example.store.storeservice.dto.StoreDto;
import com.example.store.storeservice.service.StoreService;
import com.example.util.AuthUtil;
import com.example.util.ResponseUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static com.example.Constant.HttpHeaderConstants.X_USER_ROLE;

@RestController
@RequestMapping("/api/store")
@RequiredArgsConstructor
public class StoreController {

    private final StoreService storeService;

    @PostMapping
    public ResponseEntity<ApiResponse<String>> createStore(@Valid @RequestBody StoreCreateRequest request,
                                                           @RequestHeader(X_USER_ROLE) String role) {

        //관리자 계정만 접근 가능
        AuthUtil.validateAdmin(role);

        storeService.createStore(request);
        return ResponseUtil.created("매장 생성 완료");
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<StoreDto>>> getAllStore(@RequestHeader(X_USER_ROLE) String role) {

        //관리자 계정만 접근 가능
        AuthUtil.validateAdmin(role);

        return ResponseUtil.success(storeService.getAllStore());
    }

    @GetMapping("/count/total")
    public ResponseEntity<ApiResponse<Integer>> getTotalStoreCount(@RequestHeader(X_USER_ROLE) String role) {
        AuthUtil.validateAdmin(role);
        int totalCount = storeService.getTotalStoreCount();
        return ResponseUtil.success(totalCount);
    }

    @GetMapping("/{storeId}")
    public ResponseEntity<ApiResponse<StoreDto>> getAllStore(
            @PathVariable int storeId,
            @RequestHeader(X_USER_ROLE) String role) {

        //관리자 계정만 접근 가능
        AuthUtil.validateAdmin(role);

        return ResponseUtil.success(storeService.getStore(storeId));
    }

    @PatchMapping("/{storeId}")
    public ResponseEntity<ApiResponse<String>> updateStore(@PathVariable int storeId,
                                                           @RequestBody StoreDto storeDto,
                                                           @RequestHeader(X_USER_ROLE) String role) {

        AuthUtil.validateAdmin(role);
        storeService.updateStore(storeId, storeDto);

        return ResponseUtil.success("점포정보 수정 완료");

    }

    @DeleteMapping("/{storeId}")
    public ResponseEntity<ApiResponse<String>> deleteStore(@PathVariable int storeId,
                                                           @RequestHeader(X_USER_ROLE) String role) {

        AuthUtil.validateAdmin(role);
        storeService.deleteStore(storeId);

        return ResponseUtil.success("점포 삭제 완료");

    }

}
