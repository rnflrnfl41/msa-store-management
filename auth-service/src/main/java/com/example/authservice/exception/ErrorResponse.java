package com.example.authservice.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Schema(name = "ErrorResponse", description = "에러 응답")
public class ErrorResponse {
    @Schema(description = "에러 메시지", example = "회사를 찾을 수 없습니다.")
    private String message;
    @Schema(description = "에러 코드", example = "COMPANY_NOT_FOUND")
    private String code;
    @Schema(description = "HTTP 상태코드", example = "404")
    private int status;
    @Schema(description = "에러 발생 시간", example = "2025-06-18T12:00:00")
    private LocalDateTime timestamp;

    public ErrorResponse(CommonException ex) {
        this.message = ex.getMessage();
        this.code = ex.getCode().name();
        this.status = ex.getStatus().value();
        this.timestamp = LocalDateTime.now();
    }
}
