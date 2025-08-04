package com.example.salesservice.repository;

import com.example.salesservice.entity.ErrorLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ErrorLogRepository extends JpaRepository <ErrorLog, Integer> {
}
