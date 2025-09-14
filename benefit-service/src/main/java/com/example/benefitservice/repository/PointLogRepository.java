package com.example.benefitservice.repository;

import com.example.benefitservice.entity.PointLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PointLogRepository extends JpaRepository <PointLog, Integer> {

    Optional<PointLog> findByStoreIdAndCustomerId(int storeId, int customerId);
}
