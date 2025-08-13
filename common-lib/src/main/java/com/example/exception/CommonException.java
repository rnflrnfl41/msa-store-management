package com.example.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class CommonException extends RuntimeException {
    private final HttpStatus status;
    private final CommonExceptionCode code;
    private final String customCode;

    public CommonException(CommonExceptionCode exceptionCode) {
        super(exceptionCode.getMessage());
        this.status = exceptionCode.getStatus();
        this.code = exceptionCode;
        this.customCode = null;
    }

    // enum 없이 커스텀 코드로 던질 때
    public CommonException(HttpStatus status, String customCode, String message) {
        super(message);
        this.status = status;
        this.code = null;
        this.customCode = customCode;
    }

}
