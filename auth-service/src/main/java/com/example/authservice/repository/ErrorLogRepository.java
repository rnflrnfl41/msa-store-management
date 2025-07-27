package com.example.authservice.repository;

import com.example.authservice.entity.ErrorLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ErrorLogRepository extends JpaRepository <ErrorLog, Integer> {
}
