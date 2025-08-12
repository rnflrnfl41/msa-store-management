package com.example.authservice.controller;

import com.example.authservice.dto.*;
import com.example.authservice.service.AuthService;
import com.example.dto.ApiResponse;
import com.example.util.ResponseUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping(value = "/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @RequestBody LoginRequest loginRequest,
            HttpServletResponse httpResponse) {

        LoginResponse loginResponse = authService.login(loginRequest);

        ResponseCookie refreshCookie = authService.setRefreshToken(loginRequest);
        httpResponse.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

        return ResponseUtil.success(loginResponse);
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout(HttpServletRequest request,
                                                      HttpServletResponse response) {

        // 항상 refreshToken 쿠키를 무효화하는 새 쿠키 생성
        ResponseCookie newCookie = ResponseCookie
                .from("refreshToken", null)
                .httpOnly(true)
                .secure(false) // HTTPS 환경에서만 적용 (일단 개발용으로 false)
                .sameSite("Strict")
                .path("/") // 경로 전체에서 사용
                .maxAge(0) // 유효기간 설정
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, newCookie.toString());

        return ResponseUtil.success("로그아웃 완료");
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<ApiResponse<LoginResponse>> refreshToken(HttpServletRequest request) {


        try {

            String refreshToken = Arrays.stream(request.getCookies())
                    .filter(c -> c.getName().equals("refreshToken"))
                    .map(Cookie::getValue)
                    .findFirst()
                    .orElse(null);

            LoginResponse response = authService.refreshToken(refreshToken);

            return ResponseUtil.success(response);

        }catch (Exception e){
            return null;
        }

    }

}
