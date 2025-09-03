package com.example.pointservice.service;

import com.example.pointservice.dto.CouponDto;
import com.example.pointservice.entity.Coupon;
import com.example.pointservice.repository.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CouponService {

    private final CouponRepository couponRepository;
    private final ModelMapper modelMapper;


    public List<CouponDto>  getCustomerCouponList(int storeId, int customerId) {
        return couponRepository.findByStoreIdAndCustomerId(storeId, customerId)
                .stream().map(this::convertToDto)
                .toList();
    }

    private CouponDto convertToDto(Coupon coupon) {
        return CouponDto.builder()
                .id(coupon.getId())
                .name(coupon.getName())
                .amount(String.valueOf(coupon.getAmount()))
                .type(coupon.getType())
                .createdDate(coupon.getCreatedDate().toLocalDate())
                .expiryDate(coupon.getExpiryDate())
                .used(coupon.getIsUsed() != null ? coupon.getIsUsed() : false)
                .usedDate(coupon.getUsedDate())
                .build();
    }

}
