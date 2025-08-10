package com.example.customerservice.repository;

import com.example.customerservice.entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Integer> {

    // store_id로 고객 목록 조회
    List<Customer> findByStoreId(Integer storeId);

    // store_id와 public_id로 고객 조회 (보안 강화)
    Optional<Customer> findByPublicIdAndStoreId(String publicId, Integer storeId);

    // store_id와 id로 고객 조회 (보안 강화)
    Optional<Customer> findByIdAndStoreId(Integer id, Integer storeId);

    // 통합 검색
    @Query("SELECT c FROM Customer c WHERE c.storeId = :storeId AND " +
           "(:keyword IS NULL OR c.name LIKE %:keyword% OR c.phone LIKE %:keyword%) AND " +
           "(:name IS NULL OR c.name LIKE %:name%) AND " +
           "(:phone IS NULL OR c.phone LIKE %:phone%) AND " +
           "(:startDate IS NULL OR c.createdAt >= :startDate) AND " +
           "(:endDate IS NULL OR c.createdAt <= :endDate)")
    Page<Customer> findByStoreIdAndSearchCriteria(
            @Param("storeId") Integer storeId,
            @Param("keyword") String keyword,
            @Param("name") String name,
            @Param("phone") String phone,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            Pageable pageable
    );

    // store_id로 고객 수 조회
    long countByStoreId(Integer storeId);

    // 전화번호 중복 확인 (같은 store_id 내에서)
    boolean existsByPhoneAndStoreId(String phone, Integer storeId);
} 