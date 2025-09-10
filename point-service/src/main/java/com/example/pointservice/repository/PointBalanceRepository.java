package com.example.pointservice.repository;

import com.example.pointservice.entity.PointBalance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PointBalanceRepository extends JpaRepository <PointBalance, Integer> {

    Optional<PointBalance> findByStoreIdAndCustomerId(int storedId, int customerId);

    List<PointBalance> findByStoreIdAndCustomerIdIn(Integer storeId, List<Integer> customerIds);

}
