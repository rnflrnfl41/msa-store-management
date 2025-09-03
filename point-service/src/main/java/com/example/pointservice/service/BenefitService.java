package com.example.pointservice.service;

import com.example.pointservice.dto.CouponDto;
import com.example.pointservice.dto.CustomerBenefitResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BenefitService {

    private final CouponService couponService;
    private final PointService pointService;

    public CustomerBenefitResponse getCustomerBenefitList(int storeId,int customerId) {

        int customerTotalPoint = pointService.getCustomerPoint(storeId, customerId);
        List<CouponDto> couponDtoList = couponService.getCustomerCouponList(storeId,customerId);

        return CustomerBenefitResponse
                .builder()
                .points(customerTotalPoint)
                .couponDtoList(couponDtoList)
                .build();

    }
}
