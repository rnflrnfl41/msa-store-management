package com.example.pointservice.service;

import com.example.dto.PointServiceBenefitResponse;
import com.example.dto.CustomerCoupon;
import com.example.pointservice.dto.CouponDto;
import com.example.pointservice.dto.BenefitResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BenefitService {

    private final CouponService couponService;
    private final PointService pointService;

    public BenefitResponse getCustomerBenefitList(int storeId, int customerId) {

        int customerTotalPoint = pointService.getCustomerPoint(storeId, customerId);
        List<CouponDto> couponDtoList = couponService.getCustomerCouponList(storeId,customerId);

        return BenefitResponse
                .builder()
                .points(customerTotalPoint)
                .couponDtoList(couponDtoList)
                .build();

    }

    public List<PointServiceBenefitResponse> getCustomerBenefitListBatch(Integer storeId, List<Integer> customerIds) {

        //모든 고객의 포인트를 한 번에 조회
        Map<Integer, Integer> customerPointMap = pointService.getCustomerPointsBatch(storeId, customerIds);

        //모든 고객의 쿠폰을 한 번에 조회
        Map<Integer, List<CustomerCoupon>> customerCouponMap = couponService.getCustomerCouponListBatch(storeId, customerIds);

        //각 고객별로 CustomerBenefitResponse 생성
        return customerIds.stream()
                .map(customerId -> PointServiceBenefitResponse.builder()
                        .customerId(customerId)
                        .points(customerPointMap.getOrDefault(customerId, 0))
                        .coupons(customerCouponMap.getOrDefault(customerId, List.of()))
                        .build())
                .collect(Collectors.toList());

    }
}
