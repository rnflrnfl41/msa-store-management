package com.example;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class CommonException extends RuntimeException {
    private final HttpStatus status;
    private final CommonExceptionCode code;

    public CommonException(CommonExceptionCode exceptionCode) {
        super(exceptionCode.getMessage());
        this.status = exceptionCode.getStatus();
        this.code = exceptionCode;
    }
}
