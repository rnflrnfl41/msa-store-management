package com.example.salesservice.repository;

import com.example.salesservice.entity.Payment;
import com.example.salesservice.entity.Visit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository <Payment, Integer> {

    Optional<Payment> findByVisit(Visit visit);
}
