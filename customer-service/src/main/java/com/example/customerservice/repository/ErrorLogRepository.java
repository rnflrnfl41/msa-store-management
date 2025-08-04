package com.example.customerservice.repository;

import com.example.customerservice.entity.ErrorLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ErrorLogRepository extends JpaRepository<ErrorLog, Integer> {
}
