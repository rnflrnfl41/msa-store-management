package com.example.benefitservice.service;

import com.example.benefitservice.entity.PointLog;
import com.example.exception.CommonException;
import com.example.exception.CommonExceptionCode;
import com.example.benefitservice.entity.PointBalance;
import com.example.benefitservice.repository.PointBalanceRepository;
import com.example.benefitservice.repository.PointLogRepository;
import jakarta.transaction.Transactional;
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

    @Transactional
    public void usePoint(int usedPoint, int customerId, int storeId) {

        pointLogRepository.save(PointLog.builder()
                .customerId(customerId)
                .type("use")
                .storeId(storeId)
                .pointAmount(usedPoint)
                .reason("서비스 사용")
                .build());


        pointBalanceRepository.findByStoreIdAndCustomerId(storeId, customerId)
                .map(pointBalance -> {
                    if (pointBalance.getTotalPoints() < usedPoint) {
                        throw new CommonException(CommonExceptionCode.NOT_ENOUGH_POINT);
                    }
                    pointBalance.setTotalPoints(pointBalance.getTotalPoints() - usedPoint);
                    return pointBalanceRepository.save(pointBalance);
                }).orElseThrow(() -> new CommonException(CommonExceptionCode.NOT_ENOUGH_POINT));

    }

    @Transactional
    public void rollbackUsePoint(int usedPoint, int customerId, int storeId) {

        pointLogRepository.save(PointLog.builder()
                .customerId(customerId)
                .type("rollback")
                .storeId(storeId)
                .pointAmount(usedPoint)
                .reason("서비스 사용 취소")
                .build());

        pointBalanceRepository.findByStoreIdAndCustomerId(storeId, customerId)
                .map(pointBalance -> {
                    pointBalance.setTotalPoints(pointBalance.getTotalPoints() + usedPoint);
                    return pointBalanceRepository.save(pointBalance);
                }).orElseThrow(() -> new CommonException(CommonExceptionCode.NOT_ENOUGH_POINT));

    }

}