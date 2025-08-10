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

import static com.example.Constant.HttpHeaderConstants.X_USER_STORE_ID;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;



    // 고객 생성
    @PostMapping
    public ResponseEntity<ApiResponse<CustomerResponse>> createCustomer(
            @Valid @RequestBody CustomerCreateRequest request,
            @RequestHeader(X_USER_STORE_ID) String storeIdHeader) {
        Integer storeId = Integer.parseInt(storeIdHeader);
        CustomerResponse customer = customerService.createCustomer(request, storeId);
        return ResponseUtil.created(customer);
    }

    // ID로 고객 조회
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CustomerResponse>> getCustomerById(
            @PathVariable Integer id,
            @RequestHeader(X_USER_STORE_ID) String storeIdHeader) {
        Integer storeId = Integer.parseInt(storeIdHeader);
        CustomerResponse customer = customerService.getCustomerByIdAndStoreId(id, storeId);
        return ResponseUtil.success(customer);
    }

    // public_id로 고객 조회
    @GetMapping("/public/{publicId}")
    public ResponseEntity<ApiResponse<CustomerResponse>> getCustomerByPublicId(
            @PathVariable String publicId,
            @RequestHeader(X_USER_STORE_ID) String storeIdHeader) {
        Integer storeId = Integer.parseInt(storeIdHeader);
        CustomerResponse customer = customerService.getCustomerByPublicIdAndStoreId(publicId, storeId);
        return ResponseUtil.success(customer);
    }

    // 고객 정보 수정
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CustomerResponse>> updateCustomer(
            @PathVariable Integer id,
            @Valid @RequestBody CustomerCreateRequest request,
            @RequestHeader(X_USER_STORE_ID) String storeIdHeader) {
        Integer storeId = Integer.parseInt(storeIdHeader);
        CustomerResponse updatedCustomer = customerService.updateCustomer(id, request, storeId);
        return ResponseUtil.success(updatedCustomer);
    }

    // 고객 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteCustomer(
            @PathVariable Integer id,
            @RequestHeader(X_USER_STORE_ID) String storeIdHeader) {
        Integer storeId = Integer.parseInt(storeIdHeader);
        customerService.deleteCustomer(id, storeId);
        return ResponseUtil.success("고객이 성공적으로 삭제되었습니다.");
    }

    // 통합 검색
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<CustomerResponse>>> searchCustomers(
            @ModelAttribute CustomerSearchCriteria criteria,
            @RequestHeader(X_USER_STORE_ID) String storeIdHeader) {
        Integer storeId = Integer.parseInt(storeIdHeader);
        Page<CustomerResponse> customers = customerService.searchCustomers(criteria, storeId);
        return ResponseUtil.success(customers);
    }

    // 고객 수 조회
    @GetMapping("/count")
    public ResponseEntity<ApiResponse<Long>> getCustomerCount(
            @RequestHeader(X_USER_STORE_ID) String storeIdHeader) {
        Integer storeId = Integer.parseInt(storeIdHeader);
        long count = customerService.getCustomerCountByStoreId(storeId);
        return ResponseUtil.success(count);
    }
}
