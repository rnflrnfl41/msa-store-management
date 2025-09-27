package com.example.benefitservice.controller;

import com.example.benefitservice.dto.CouponDto;
import com.example.benefitservice.dto.CouponRegistrationDto;
import com.example.dto.ApiResponse;
import com.example.dto.BenefitServiceBenefitResponse;
import com.example.benefitservice.dto.BenefitResponse;
import com.example.benefitservice.service.BenefitService;
import com.example.dto.BenefitUseRequest;
import com.example.util.ResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.Constant.BenefitConstant.BENEFIT_USE_COMPLETE;
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

    /**
     * 포인트,쿠폰 사용
     * POST /api/benefit/use
     * Body: [customerId1, customerId2, ...]
     */
    @PostMapping("/use")
    public ResponseEntity<ApiResponse<String>> useBenefits(
            @RequestBody BenefitUseRequest request,
            @RequestHeader(X_USER_STORE_ID) String storeIdHeader
    ) {
        Integer storeId = Integer.parseInt(storeIdHeader);
        benefitService.useBenefits(storeId, request);
        return ResponseUtil.success(BENEFIT_USE_COMPLETE);
    }

    /**
     * 포인트,쿠폰 사용된거 롤백
     * POST /api/benefit/use
     * Body: [customerId1, customerId2, ...]
     */
    @PostMapping("/use/rollback")
    public ResponseEntity<ApiResponse<String>> useBenefitsRollback(
            @RequestBody BenefitUseRequest request,
            @RequestHeader(X_USER_STORE_ID) String storeIdHeader
    ) {
        Integer storeId = Integer.parseInt(storeIdHeader);
        benefitService.rollbackUseBenefits(storeId, request);
        return ResponseUtil.success(
                String.format("포인트 및 쿠폰 롤백 완료 - 포인트: %d, 쿠폰ID: %s",
                request.getUsedPoint(), request.getUsedCouponId())
        );
    }

    /**
     * 모든 쿠폰 조회
     * GET /api/benefit/coupon/all
     * @param storeIdHeader
     * @return
     */
    @GetMapping("/coupon/all")
    public ResponseEntity<ApiResponse<List<CouponDto>>> getCustomerCouponListBatch(
            @RequestHeader(X_USER_STORE_ID) String storeIdHeader
    ) {
        Integer storeId = Integer.parseInt(storeIdHeader);
        return ResponseUtil.success(benefitService.getAllCouponList(storeId));
    }

    /**
     * 고객별 쿠폰 조회
     * GET /api/benefit/coupon/{customerId}
     * @param customerId
     * @param storeIdHeader
     * @return
     */
    @GetMapping("/coupon/{customerId}")
    public ResponseEntity<ApiResponse<List<CouponDto>>> getCustomerCouponList(
            @PathVariable Integer customerId,
            @RequestHeader(X_USER_STORE_ID) String storeIdHeader
    ) {
        Integer storeId = Integer.parseInt(storeIdHeader);
        return ResponseUtil.success(benefitService.getCustomerCouponList(storeId, customerId));
    }

    @PostMapping("/coupon")
    public ResponseEntity<ApiResponse<String>> createCoupon(
            @RequestBody CouponRegistrationDto  couponDto,
            @RequestHeader(X_USER_STORE_ID) String storeIdHeader
    ) {
        Integer storeId = Integer.parseInt(storeIdHeader);
        benefitService.createCoupon(storeId, couponDto);
        return ResponseUtil.created("쿠폰 생성 완료");
    }

    /**
     * 쿠폰 삭제
     * DELETE /api/benefit/coupon/{couponId}
     * @param couponId
     * @param storeIdHeader
     * @return
     */
    @DeleteMapping("coupon/{couponId}")
    public ResponseEntity<ApiResponse<String>> deleteCoupon(
            @PathVariable String couponId,
            @RequestHeader(X_USER_STORE_ID) String storeIdHeader
    ) {
        Integer storeId = Integer.parseInt(storeIdHeader);
        benefitService.deleteCoupon(storeId, couponId);
        return ResponseUtil.success("쿠폰 삭제 완료");
    }


}