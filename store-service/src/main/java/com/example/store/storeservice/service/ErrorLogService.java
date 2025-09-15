package com.example.store.storeservice.service;

import com.example.store.storeservice.entity.ErrorLog;
import com.example.store.storeservice.repository.ErrorLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static com.example.Constant.ServiceConstants.INTERNAL_SERVICE_ERROR_CODE;

@Service
@RequiredArgsConstructor
@Slf4j
public class ErrorLogService {
    
    private final ErrorLogRepository errorLogRepository;
    
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void internalDeleteErrorLogSave(int storeId, Exception error) {

        String requestParams = "{\"storeId\": \"" + storeId + "\"}";

        ErrorLog errorLog = ErrorLog.builder()
                .errorId(UUID.randomUUID().toString())
                .message(error.getMessage())
                .code(INTERNAL_SERVICE_ERROR_CODE)
                .status(500)
                .uri("/api/store/" + storeId)
                .method("DELETE")
                .stackTrace(ExceptionUtils.getStackTrace(error))
                .requestParams(requestParams)
                .build();

        errorLogRepository.save(errorLog);
    }
}
