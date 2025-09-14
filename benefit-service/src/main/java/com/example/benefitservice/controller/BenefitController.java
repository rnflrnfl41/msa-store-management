package com.example.benefitservice.controller;

import com.example.dto.ApiResponse;
import com.example.dto.BenefitServiceBenefitResponse;
import com.example.benefitservice.dto.BenefitResponse;
import com.example.benefitservice.service.BenefitService;
import com.example.util.ResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.Constant.HttpHeaderConstants.X_USER_STORE_ID;

@RestController
@RequestMapping("/api/benefit")
@RequiredArgsConstructor
public class BenefitController {

    private final BenefitService benefitService;

    /**
     * 단일 고객의 혜택 정보 조회 (포인트 + 쿠폰)
     * GET /api/benefit/{customerId}
     */
    @GetMapping("/{customerId}")
    public ResponseEntity<ApiResponse<BenefitResponse>> getCustomerBenefitList(
            @PathVariable Integer customerId,
            @RequestHeader(X_USER_STORE_ID) String storeIdHeader
    ) {
        Integer storeId = Integer.parseInt(storeIdHeader);
        return ResponseUtil.success(benefitService.getCustomerBenefitList(storeId, customerId));
    }

    /**
     * 여러 고객의 혜택 정보 일괄 조회 (포인트 + 쿠폰)
     * POST /api/benefit/batch
     * Body: [customerId1, customerId2, ...]
     */
    @PostMapping("/batch")
    public ResponseEntity<ApiResponse<List<BenefitServiceBenefitResponse>>> getCustomerBenefitListBatch(
            @RequestBody List<Integer> customerIds,
            @RequestHeader(X_USER_STORE_ID) String storeIdHeader
    ) {
        Integer storeId = Integer.parseInt(storeIdHeader);
        return ResponseUtil.success(benefitService.getCustomerBenefitListBatch(storeId, customerIds));
    }

}