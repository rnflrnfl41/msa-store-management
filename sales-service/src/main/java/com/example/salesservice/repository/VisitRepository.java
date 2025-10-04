package com.example.salesservice.repository;

import com.example.dto.FinancialChartData;
import com.example.dto.FinancialSummary;
import com.example.salesservice.entity.Visit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface VisitRepository extends JpaRepository <Visit, Integer> {

    List<Visit> findByStoreIdAndCustomerId(int storeId, int customerId);

    @Query("select new com.example.dto.FinancialSummary(" +
            "COALESCE(sum(v.finalServiceAmount),0),count(v) " +
            ")" +
            "from Visit v where v.visitDate = :date and v.storeId = :storeId")
    FinancialSummary getSummarySalesDate(LocalDate date, int storeId);

    @Query("select new com.example.dto.FinancialSummary(" +
            "COALESCE(sum(v.finalServiceAmount),0), count(v)" +
            ")" +
            " from Visit v where month(v.visitDate) = :month and v.storeId = :storeId ")
    FinancialSummary getSummarySalesMonth(int month, int storeId);

    // 월별 차트 데이터
    @Query("select new com.example.dto.FinancialChartData(" +
            "v.visitDate, " +
            "COALESCE(sum(v.finalServiceAmount),0), " +
            "count(v)" +
            ")" +
            " from Visit v where v.storeId = :storeId and " +
            " v.visitDate between :startDate and :endDate " +
            " group by year(v.visitDate), month(v.visitDate) " +
            " order by year(v.visitDate), month(v.visitDate)")
    List<FinancialChartData> getMonthlyChartDataByPeriod(LocalDate startDate, LocalDate endDate, int storeId);

    // 일별 차트 데이터
    @Query("select new com.example.dto.FinancialChartData(" +
            "v.visitDate, " +
            "COALESCE(sum(v.finalServiceAmount),0), " +
            "count(v)" +
            ")" +
            " from Visit v where v.storeId = :storeId and " +
            " v.visitDate between :startDate and :endDate " +
            " group by v.visitDate " +
            " order by v.visitDate")
    List<FinancialChartData> getDailyChartDataByPeriod(LocalDate startDate, LocalDate endDate, int storeId);

    Page<Visit> findByVisitDateAndStoreId(LocalDate visitDate, int storeId, Pageable pageable);

    Optional<Visit> findByIdAndStoreId(int visitId, Integer storeId);
}
