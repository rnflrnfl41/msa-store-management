package com.example.benefitservice.service;

import com.example.benefitservice.dto.CouponRegistrationDto;
import com.example.dto.CustomerCoupon;
import com.example.benefitservice.dto.CouponDto;
import com.example.benefitservice.entity.Coupon;
import com.example.benefitservice.repository.CouponRepository;
import com.example.exception.CommonException;
import com.example.exception.CommonExceptionCode;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

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
                .amount(coupon.getAmount())
                .type(coupon.getType())
                .createdDate(coupon.getCreatedDate().toLocalDate())
                .expiryDate(coupon.getExpiryDate())
                .used(coupon.getIsUsed() != null ? coupon.getIsUsed() : false)
                .usedDate(coupon.getUsedDate())
                .customerId(coupon.getCustomerId())
                .customerName(coupon.getCustomerName())
                .build();
    }



    public Map<Integer, List<CustomerCoupon>> getCustomerCouponListBatch(Integer storeId, List<Integer> customerIds) {

        List<Coupon> couponList = couponRepository.findValidCouponsByStoreIdAndCustomerIdIn(storeId, customerIds);

        return couponList.stream()
                .collect(Collectors.groupingBy(
                        Coupon::getCustomerId,
                        Collectors.mapping(
                                coupon -> modelMapper.map(coupon, CustomerCoupon.class),
                                Collectors.toList()
                        )
                ));

    }


    public void useCoupon(String usedCouponId, int customerId, Integer storeId) {

        couponRepository.getCoupon(storeId,customerId,usedCouponId)
                .map(coupon -> {
                    coupon.setIsUsed(true);
                    coupon.setUsedDate(LocalDate.now());
                    return couponRepository.save(coupon);
                })
                .orElseThrow(() -> new CommonException(CommonExceptionCode.NO_COUPON));

    }

    public void rollbackUseCoupon(String usedCouponId, int customerId, Integer storeId) {

        couponRepository.getUsedCoupon(storeId, customerId, usedCouponId)
                .map(coupon -> {
                    coupon.setIsUsed(false);
                    coupon.setUsedDate(null);
                    return couponRepository.save(coupon);
                })
                .orElseThrow(() -> new CommonException(CommonExceptionCode.NO_COUPON));

    }

    public  List<CouponDto> getAllCouponList(Integer storeId) {

        List<Coupon> couponList = couponRepository.findByStoreId(storeId);

        return couponList.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

    }

    public void deleteCoupon(Integer storeId, UUID couponId) {
        couponRepository.deleteByIdAndStoreId(couponId,storeId);
    }

    public void createCoupon(Integer storeId, CouponRegistrationDto couponDto) {

        Coupon coupon = modelMapper.map(couponDto, Coupon.class);
        coupon.setStoreId(storeId);
        coupon.setIsUsed(false);
        coupon.setCreatedDate(LocalDateTime.now());
        couponRepository.save(coupon);

    }
}
