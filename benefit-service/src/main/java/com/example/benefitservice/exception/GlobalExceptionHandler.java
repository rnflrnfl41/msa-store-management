package com.example.benefitservice.exception;

import com.example.dto.ApiResponse;
import com.example.exception.CommonException;
import com.example.exception.CommonExceptionCode;
import com.example.util.ResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * Handle CommonException
     */
    @ExceptionHandler(CommonException.class)
    public ResponseEntity<ApiResponse<Void>> handleCommonException(CommonException ex) {
        log.error("CommonException occurred: {}", ex.getMessage(), ex);
        return ResponseEntity.status(ex.getCode().getStatus())
                .body(new ApiResponse<>(ex.getCode().getStatus().value(), ex.getMessage(), null, java.time.LocalDateTime.now()));
    }

    /**
     * Handle validation errors from @Valid annotation in controller API
     * When validation fails, MethodArgumentNotValidException is thrown
     * Extract message from ex.getBindingResult() and return response
     * Required dependency: org.springframework.boot:spring-boot-starter-validation
     * Usage: Add @Valid annotation to DTO parameters in controller methods
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationException(MethodArgumentNotValidException ex) {
        log.error("Validation error occurred: {}", ex.getMessage(), ex);
        
        String errorMessage = "Validation failed: " + ex.getBindingResult().getFieldError().getDefaultMessage();
        
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> 
            errors.put(error.getField(), error.getDefaultMessage())
        );
        
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)  // 400 Bad Request status code
                .body(new ApiResponse<>(400, errorMessage, errors, java.time.LocalDateTime.now()));
    }

    /**
     * Handle all other exceptions
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception ex) {
        log.error("Unexpected error occurred: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>(500, "Internal server error", null, java.time.LocalDateTime.now()));
    }
}