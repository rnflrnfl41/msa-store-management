package com.example.customerservice.controller;

import com.example.customerservice.dto.CustomerCreateRequest;
import com.example.customerservice.dto.CustomerResponse;
import com.example.customerservice.dto.CustomerSearchCriteria;
import com.example.customerservice.service.CustomerService;
import com.example.dto.ApiResponse;
import com.example.util.ResponseUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.Constant.HttpHeaderConstants.X_USER_STORE_ID;

@RestController
@RequestMapping("/api/customer")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;



    // 고객 생성
    @PostMapping
    public ResponseEntity<ApiResponse<CustomerResponse>> createCustomer(
            @Valid @RequestBody CustomerCreateRequest request,
            @RequestHeader(X_USER_STORE_ID) String storeIdHeader
    ) {
        Integer storeId = Integer.parseInt(storeIdHeader);
        CustomerResponse response = customerService.createCustomer(request, storeId);
        return ResponseUtil.created(response);
    }

//    // ID로 고객 조회
//    @GetMapping("/{id}")
//    public ResponseEntity<ApiResponse<CustomerResponse>> getCustomerById(
//            @PathVariable Integer id,
//            @RequestHeader(X_USER_STORE_ID) String storeIdHeader) {
//        Integer storeId = Integer.parseInt(storeIdHeader);
//        CustomerResponse customer = customerService.getCustomerByIdAndStoreId(id, storeId);
//        return ResponseUtil.success(customer);
//    }
//
//    // public_id로 고객 조회
//    @GetMapping("/public/{publicId}")
//    public ResponseEntity<ApiResponse<CustomerResponse>> getCustomerByPublicId(
//            @PathVariable String publicId,
//            @RequestHeader(X_USER_STORE_ID) String storeIdHeader) {
//        Integer storeId = Integer.parseInt(storeIdHeader);
//        CustomerResponse customer = customerService.getCustomerByPublicIdAndStoreId(publicId, storeId);
//        return ResponseUtil.success(customer);
//    }

    // 고객 정보 업데이트
    @PutMapping("/{customerId}")
    public ResponseEntity<ApiResponse<String>> updateCustomer(
            @PathVariable Integer customerId,
            @Valid @RequestBody CustomerCreateRequest request,
            @RequestHeader(X_USER_STORE_ID) String storeIdHeader
    ) {
        Integer storeId = Integer.parseInt(storeIdHeader);
        customerService.updateCustomer(customerId, request, storeId);
        return ResponseUtil.success("고객 수정 완료");
    }

    // 고객 삭제
    @DeleteMapping("/{customerId}")
    public ResponseEntity<ApiResponse<String>> deleteCustomer(
            @PathVariable Integer customerId,
            @RequestHeader(X_USER_STORE_ID) String storeIdHeader
    ) {
        Integer storeId = Integer.parseInt(storeIdHeader);
        customerService.deleteCustomer(customerId, storeId);
        return ResponseUtil.success("고객이 성공적으로 삭제되었습니다.");
    }

    // 통합 검색
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<CustomerResponse>>> searchCustomers(
            @RequestHeader(X_USER_STORE_ID) String storeIdHeader
    ) {
        Integer storeId = Integer.parseInt(storeIdHeader);
        List<CustomerResponse> customers = customerService.searchAllCustomers(storeId);
        return ResponseUtil.success(customers);
    }

    // 고객 수 조회
    @GetMapping("/count")
    public ResponseEntity<ApiResponse<Long>> getCustomerCount(
            @RequestHeader(X_USER_STORE_ID) String storeIdHeader
    ) {
        Integer storeId = Integer.parseInt(storeIdHeader);
        long count = customerService.getCustomerCountByStoreId(storeId);
        return ResponseUtil.success(count);
    }
}
