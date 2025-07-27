package com.example.authservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
@Schema(description = "로그인 성공시 반환 DTO")
public class LoginResponse {

    @Schema(description = "사용자 idx", example = "1")
    private int userIdx;

    @Schema(description = "사용자 이름", example = "시스템관리자")
    private String userName;

    @Schema(description = "사용자 아이디", example = "99999")
    private String userId;

    @Schema(description = "토큰", example = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwibG9naW5JZCI6Ijk5OTk5IiwiY29tcGFueUNvZGUiOiIyMDI1MDEwMjAxIiwiaWF0IjoxNzUwMjI4MjczLCJleHAiOjE3NTAyNTcwNzN9.rPsO9gPA71A7FryZAafKBokTlxae85p_Wlq-CEiCkXo")
    private String token;

}
