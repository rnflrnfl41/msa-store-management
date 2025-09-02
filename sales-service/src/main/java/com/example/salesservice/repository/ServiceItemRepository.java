package com.example.salesservice.repository;

import com.example.salesservice.entity.ServiceItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ServiceItemRepository extends JpaRepository<ServiceItem, Integer> {

    List<ServiceItem> findByVisitId(int visitId);
}
