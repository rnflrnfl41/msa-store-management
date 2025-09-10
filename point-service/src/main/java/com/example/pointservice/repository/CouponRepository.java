package com.example.pointservice.repository;

import com.example.pointservice.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CouponRepository extends JpaRepository <Coupon, UUID> {

    List<Coupon> findByStoreIdAndCustomerId(int storeId, int customerId);

    List<Coupon> findByStoreIdAndCustomerIdIn(int storeId, List<Integer> customerIds);

}
