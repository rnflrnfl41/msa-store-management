package com.example.pointservice.service;

import com.example.exception.CommonException;
import com.example.exception.CommonExceptionCode;
import com.example.pointservice.entity.PointBalance;
import com.example.pointservice.repository.PointBalanceRepository;
import com.example.pointservice.repository.PointLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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

}
