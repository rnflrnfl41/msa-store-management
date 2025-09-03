package com.example.pointservice.repository;

import com.example.pointservice.entity.PointLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PointLogRepository extends JpaRepository <PointLog, Integer> {
}
