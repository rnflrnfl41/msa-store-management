package com.example.benefitservice.service;

import com.example.exception.CommonException;
import com.example.exception.CommonExceptionCode;
import com.example.benefitservice.entity.PointBalance;
import com.example.benefitservice.repository.PointBalanceRepository;
import com.example.benefitservice.repository.PointLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PointService {

    private final PointLogRepository pointLogRepository;
    private final PointBalanceRepository pointBalanceRepository;

    public int getCustomerPoint(int customerId, Integer storeId) {
        return pointBalanceRepository
                .findByStoreIdAndCustomerId(customerId, storeId)
                .map(PointBalance::getTotalPoints).orElse(0);
    }

    public Map<Integer, Integer> getCustomerPointsBatch(Integer storeId, List<Integer> customerIds) {

        List<PointBalance> pointBalances = pointBalanceRepository
                .findByStoreIdAndCustomerIdIn(storeId, customerIds);

        // Map으로 변환 (customerId -> totalPoints)
        return pointBalances.stream()
                .collect(Collectors.toMap(
                        PointBalance::getCustomerId,
                        PointBalance::getTotalPoints
                ));

    }
}