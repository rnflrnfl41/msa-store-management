package com.example.salesservice.repository;

import com.example.salesservice.entity.Visit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VisitRepository extends JpaRepository <Visit, Integer> {

    List<Visit> findByStoreIdAndCustomerId(int storeId, int customerId);

}
