package com.example.benefitservice.repository;

import com.example.benefitservice.entity.ErrorLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ErrorLogRepository extends JpaRepository <ErrorLog, Integer> {
}
