package com.example.authservice.exception;

import com.example.exception.CommonException;
import com.example.dto.ErrorResponse;
import com.example.authservice.entity.ErrorLog;
import com.example.authservice.repository.ErrorLogRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.UUID;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final ErrorLogRepository errorLogRepository;
    private final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(CommonException.class)
    public ResponseEntity<ErrorResponse> handleCommonException(CommonException ex) {
        return ResponseEntity
                .status(ex.getStatus())
                .body(new ErrorResponse(ex));
    }

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<ErrorResponse> handleThrowable(Throwable ex, HttpServletRequest request) {

        String errorId = UUID.randomUUID().toString();

        log.error("[{}] Internal server error at {} {}", errorId, request.getMethod(), request.getRequestURI(), ex);

        errorLogRepository.save(ErrorLog.builder()
                .errorId(errorId)
                .message(ex.getMessage())
                .code("INTERNAL_SERVER_ERROR")
                .status(500)
                .uri(request.getRequestURI())
                .method(request.getMethod())
                .stackTrace(ExceptionUtils.getStackTrace(ex))
                .build());

        return ResponseEntity.status(500)
                .body(new ErrorResponse(
                        "서버 내부 오류가 발생했습니다. (에러 ID: " + errorId + ")",
                        "INTERNAL_SERVER_ERROR",
                        500,
                        LocalDateTime.now()
                ));
    }

}
