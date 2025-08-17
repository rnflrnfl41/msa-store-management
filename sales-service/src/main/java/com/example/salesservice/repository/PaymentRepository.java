package com.example.salesservice.repository;

import com.example.salesservice.entity.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Integer> {

    Page<Payment> findByVisitId(Integer visitId, Pageable pageable);

    Page<Payment> findByPaymentMethod(String paymentMethod, Pageable pageable);

    Page<Payment> findByCreatedAtBetween(Instant startDate, Instant endDate, Pageable pageable);

    @Query("SELECT p FROM Payment p WHERE p.visitId = :visitId AND p.createdAt BETWEEN :startDate AND :endDate")
    Page<Payment> findByVisitIdAndDateRange(@Param("visitId") Integer visitId,
                                           @Param("startDate") Instant startDate,
                                           @Param("endDate") Instant endDate,
                                           Pageable pageable);

    @Query("SELECT p FROM Payment p WHERE p.amount BETWEEN :minAmount AND :maxAmount")
    Page<Payment> findByAmountRange(@Param("minAmount") Integer minAmount,
                                   @Param("maxAmount") Integer maxAmount,
                                   Pageable pageable);

    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.visitId = :visitId AND p.createdAt BETWEEN :startDate AND :endDate")
    Integer getTotalSalesByVisitAndDateRange(@Param("visitId") Integer visitId,
                                            @Param("startDate") Instant startDate,
                                            @Param("endDate") Instant endDate);

    @Query("SELECT COUNT(p) FROM Payment p WHERE p.visitId = :visitId AND p.createdAt BETWEEN :startDate AND :endDate")
    Long getPaymentCountByVisitAndDateRange(@Param("visitId") Integer visitId,
                                           @Param("startDate") Instant startDate,
                                           @Param("endDate") Instant endDate);

    List<Payment> findByVisitIdOrderByCreatedAtDesc(Integer visitId);
}
