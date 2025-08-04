package com.example.expenseservice.repository;

import com.example.expenseservice.dto.ErrorLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ErrorLogRepository extends JpaRepository <ErrorLog, Integer> {
}
