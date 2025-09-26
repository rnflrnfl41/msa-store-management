package com.example.benefitservice.repository;

import com.example.benefitservice.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CouponRepository extends JpaRepository <Coupon, UUID> {

    List<Coupon> findByStoreIdAndCustomerId(int storeId, int customerId);

    @Query("select c from Coupon c where c.storeId = :storeId and c.customerId in :customerIds and c.isUsed = false and c.expiryDate >= CURRENT_DATE")
    List<Coupon> findValidCouponsByStoreIdAndCustomerIdIn(int storeId, List<Integer> customerIds);

    @Query("select c from Coupon c " +
            "where c.storeId = :storeId " +
            "and c.customerId = :customerId " +
            "and c.id = :couponId " +
            "and c.isUsed = false " +
            "and c.expiryDate >= CURRENT_DATE ")
    Optional<Coupon> getCoupon(int storeId, int customerId, String couponId);

    @Query("select c from Coupon c " +
            "where c.storeId = :storeId " +
            "and c.customerId = :customerId " +
            "and c.id = :couponId " +
            "and c.isUsed = true ")
    Optional<Coupon> getUsedCoupon(int storeId, int customerId, String couponId);

    List<Coupon> findByStoreId(Integer storeId);
}
