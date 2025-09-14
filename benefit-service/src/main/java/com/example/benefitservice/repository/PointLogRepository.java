package com.example.benefitservice.repository;

import com.example.benefitservice.entity.PointLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PointLogRepository extends JpaRepository <PointLog, Integer> {
}
