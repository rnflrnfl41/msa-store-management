package com.example.pointservice.controller;

import com.example.dto.ApiResponse;
import com.example.pointservice.dto.CustomerBenefitResponse;
import com.example.pointservice.service.BenefitService;
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

    @GetMapping("/{customerId}")
    public ResponseEntity<ApiResponse<CustomerBenefitResponse>> getCustomerBenefitList(
            @PathVariable Integer customerId,
            @RequestHeader(X_USER_STORE_ID) String storeIdHeader
    ) {
        Integer storeId = Integer.parseInt(storeIdHeader);
        return ResponseUtil.success(benefitService.getCustomerBenefitList(storeId, customerId));
    }

}
