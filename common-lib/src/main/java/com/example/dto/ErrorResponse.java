package com.example.dto;

import com.example.exception.CommonException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ErrorResponse {

    private String message;

    private String code;

    private int status;

    private LocalDateTime timestamp;

    public ErrorResponse(CommonException ex) {
        this.message = ex.getMessage();
        this.code = ex.getCode() != null ? ex.getCode().name() : ex.getCustomCode();
        this.status = ex.getStatus().value();
        this.timestamp = LocalDateTime.now();
    }
}
