package com.example.authservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Builder
@Data
@Schema(description = "로그인 성공시 반환 DTO")
public class LoginResponse {

    @Schema(description = "idx", example = "UUID")
    private UUID userId;

    @Schema(description = "매장명", example = "숙성21")
    private UUID storeId;

    @Schema(description = "사용자이름", example = "kim")
    private String userName;

    @Schema(description = "로그인 아이디", example = "99999")
    private String loginId;

    @Schema(description = "토큰", example = "xNzUwMjI4MjczLCJleHAiOjE3NTAyNTcwNzN9.rPsO9gPA71A7FryZAafKBokTlxae85p_Wlq-CEiCkXo")
    private String token;

    @Schema(description = "새로고침 토큰", example = "eyJhbGciOiJIUzIlxae85p_Wlq-CEiCkXo")
    private String refreshToken;

}
