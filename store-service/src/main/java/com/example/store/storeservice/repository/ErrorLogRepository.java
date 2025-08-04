package com.example.store.storeservice.repository;

import com.example.store.storeservice.entity.ErrorLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ErrorLogRepository extends JpaRepository <ErrorLog, Integer> {
}
