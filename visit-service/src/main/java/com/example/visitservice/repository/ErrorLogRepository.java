package com.example.visitservice.repository;

import com.example.visitservice.entity.ErrorLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ErrorLogRepository extends JpaRepository<ErrorLog, Integer> {
}
