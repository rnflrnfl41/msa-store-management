package com.example.pointservice.repository;

import com.example.pointservice.entity.Point;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PointRepository extends JpaRepository <Point, Integer> {
}
