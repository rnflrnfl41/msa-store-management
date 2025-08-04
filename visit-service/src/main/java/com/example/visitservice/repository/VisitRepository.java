package com.example.visitservice.repository;

import com.example.visitservice.entity.Visit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VisitRepository extends JpaRepository <Visit, Integer> {
}
