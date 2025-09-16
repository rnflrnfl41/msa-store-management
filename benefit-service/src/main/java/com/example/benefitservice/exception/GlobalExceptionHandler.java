package com.example.benefitservice.exception;

import com.example.benefitservice.entity.ErrorLog;
import com.example.benefitservice.repository.ErrorLogRepository;
import com.example.dto.ApiResponse;
import com.example.dto.ErrorResponse;
import com.example.exception.CommonException;
import com.example.exception.CommonExceptionCode;
import com.example.util.ResponseUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class GlobalExceptionHandler {

    private final ErrorLogRepository errorLogRepository;


    @ExceptionHandler(CommonException.class)
    public ResponseEntity<ErrorResponse> handleCommonException(CommonException ex) {
        log.error("CommonException: {}", ex.getMessage());
        return ResponseEntity
                .status(ex.getStatus())
                .body(new ErrorResponse(ex));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {

        String errorMessage = "다음 정보를 입력해주세요: " +
                ex.getBindingResult().getFieldErrors().stream()
                        .map(error -> error.getDefaultMessage())
                        .collect(Collectors.joining(", "));

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)  // 또는 적절한 상태 코드
                .body(new ErrorResponse(
                        errorMessage,
                        "INVALID_PARAMETER",
                        400,  // 또는 적절한 상태 코드
                        LocalDateTime.now()
                ));

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
                .requestParams(request.getQueryString())
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