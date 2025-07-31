package com.example.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CommonExceptionCode {

    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰 입니다."),
    MISSING_TOKEN(HttpStatus.UNAUTHORIZED, "토큰이 없습니다."),
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "토큰이 만료 되었습니다."),


    DUPLICATE_LOGIN_ID(HttpStatus.CONFLICT, "이미 사용중인 아이디 입니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
    ID_PASSWORD_FAIL(HttpStatus.BAD_REQUEST, "비밀번호가 올바르지않습니다."),


    INVALID_PARAMETER(HttpStatus.BAD_REQUEST, "파라미터가 올바르지않습니다.");

    private final HttpStatus status;
    private final String message;

}
