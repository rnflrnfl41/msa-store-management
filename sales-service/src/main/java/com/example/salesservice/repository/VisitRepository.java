package com.example.salesservice.repository;

import com.example.salesservice.dto.SalesSummary;
import com.example.salesservice.entity.Visit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface VisitRepository extends JpaRepository <Visit, Integer> {

    List<Visit> findByStoreIdAndCustomerId(int storeId, int customerId);

    @Query("select new com.example.salesservice.dto.SalesSummary(" +
            "COALESCE(sum(v.finalServiceAmount),0) as amount, count(v) as count" +
            ")" +
            "from Visit v where v.visitDate = :date and v.storeId = :storeId")
    SalesSummary getSummarySalesDate(LocalDate date, int storeId);

    @Query("select new com.example.salesservice.dto.SalesSummary(" +
            "COALESCE(sum(v.finalServiceAmount),0) as amount, count(v) as count" +
            ")" +
            " from Visit v where month(v.visitDate) = :month and v.storeId = :storeId ")
    SalesSummary getSummarySalesMonth(int month, int storeId);

}
