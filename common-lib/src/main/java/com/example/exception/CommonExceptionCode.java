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

    SAVED_REFRESH_TOKEN_NOT_EXIST(HttpStatus.UNAUTHORIZED, "저장된 refresh 토큰이 없습니다."),
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 refresh 토큰 입니다."),
    MISSING_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "refresh 토큰이 없습니다."),
    EXPIRED_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "refresh 토큰이 만료 되었습니다."),

    NO_COOKIES(HttpStatus.NOT_FOUND,"쿠키를 찾을수 없습니다."),

    DUPLICATE_PHONE_NUM(HttpStatus.CONFLICT, "이미 사용중인 휴대폰번호 입니다."),


    DUPLICATE_LOGIN_ID(HttpStatus.CONFLICT, "이미 사용중인 아이디 입니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
    ID_PASSWORD_FAIL(HttpStatus.BAD_REQUEST, "비밀번호가 올바르지않습니다."),

    NO_PERMISSIONS(HttpStatus.INTERNAL_SERVER_ERROR,"접근 권한이 없습니다."),

    STORE_NOT_FOUND(HttpStatus.NOT_FOUND,"해당하는 점포가 없습니다."),
    STORE_CREATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "점포 생성에 실패했습니다."),
    STORE_UPDATE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "점포 수정에 실패했습니다."),
    STORE_DELETION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "점포 삭제에 실패했습니다."),
    EXTERNAL_SERVICE_CALL_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "외부 서비스 호출에 실패했습니다."),

    NO_STORE_ID(HttpStatus.NOT_FOUND,"점포 아이디가 없습니다"),

    CUSTOMER_NOT_FOUND(HttpStatus.NOT_FOUND, "고객을 찾을 수 없습니다."),

    BENEFIT_USE_FAILED(HttpStatus.NOT_FOUND, "쿠폰 및 포인트 사용 실패."),

    NOT_ENOUGH_POINT(HttpStatus.INTERNAL_SERVER_ERROR, "포인트가 충분하지 않습니다."),

    NO_COUPON(HttpStatus.NOT_FOUND, "해당 쿠폰을 찾을 수 없습니다."),

    NO_VISIT_ID(HttpStatus.NOT_FOUND, "해당 아이디를 가진 매출 기록을 찾을 수 없습니다."),

    COUPON_ALREADY_USED(HttpStatus.BAD_REQUEST, "이미 사용된 쿠폰입니다."),

    INVALID_PARAMETER(HttpStatus.BAD_REQUEST, "파라미터가 올바르지않습니다.");

    private final HttpStatus status;
    private final String message;

}
