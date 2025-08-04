package com.example.pointservice.repository;

import com.example.pointservice.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CouponRepository extends JpaRepository <Coupon, UUID> {
}
