package com.example.authservice.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CommonExceptionCode {

    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
    ID_PASSWORD_FAIL(HttpStatus.BAD_REQUEST, "비밀번호가 올바르지않습니다.");

    private final HttpStatus status;
    private final String message;

}
