package com.example.store.storeservice.service;

import com.example.Constant.ServiceConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class ExternalServiceOrchestrator {
    
    private final AuthServiceClient authServiceClient;
    
    public void deleteRelatedData(int storeId) {
        List<ServiceOperation> operations = Arrays.asList(
            new ServiceOperation(ServiceConstants.AUTH_SERVICE, () -> deleteAuthUsers(storeId)),
            new ServiceOperation(ServiceConstants.CUSTOMER_SERVICE, () -> deleteCustomers(storeId)),
            new ServiceOperation(ServiceConstants.VISIT_SERVICE, () -> deleteVisits(storeId))
        );
        
        executeOperationsSequentially(operations, storeId);
    }
    
    private void executeOperationsSequentially(List<ServiceOperation> operations, int storeId) {
        for (int i = 0; i < operations.size(); i++) {
            ServiceOperation operation = operations.get(i);

            try {
                operation.execute();
                log.info("{} 완료: {}", operation.name(), storeId);

            } catch (Exception e) {
                log.error("{} 실패: {}", operation.name(), storeId, e);

                // 원본 에러를 그대로 전달 (간단하게)
                throw new RuntimeException("외부 서비스 호출 실패: " + operation.name(), e);
            }
        }
    }
    
    private void deleteAuthUsers(int storeId) {
        authServiceClient.deleteUserByStoreId(storeId);
    }
    
    private void deleteCustomers(int storeId) {
        // customerServiceClient.deleteCustomersByStoreId(storeId);
        // TODO: CustomerServiceClient 구현 후 활성화
        log.info("Customer Service 고객 삭제 스킵 (구현 예정): {}", storeId);
    }
    
    private void deleteVisits(int storeId) {
        // visitServiceClient.deleteVisitsByStoreId(storeId);
        // TODO: VisitServiceClient 구현 후 활성화
        log.info("Visit Service 방문 기록 스킵 (구현 예정): {}", storeId);
    }
    
    // 내부 클래스들
    @FunctionalInterface
    private interface ServiceExecutor {
        void execute() throws Exception;
    }
    
    private record ServiceOperation(String name, ServiceExecutor executor) {
        public void execute() throws Exception {
            executor.execute();
        }
    }
}
