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

import java.time.Duration;
import java.util.Arrays;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // 관리자용 (웹)
    @PostMapping("/admin/login")
    public ResponseEntity<ApiResponse<LoginResponse>> adminLogin(
            @RequestBody LoginRequest loginRequest,
            HttpServletResponse httpResponse) {

        LoginResponse loginResponse = authService.login(loginRequest);

        // 웹용: 쿠키 설정
        ResponseCookie refreshCookie = authService.setRefreshToken(loginRequest);
        httpResponse.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

        return ResponseUtil.success(loginResponse);
    }

    @PostMapping("/admin/logout")
    public ResponseEntity<ApiResponse<String>> adminLogout(HttpServletResponse response) {

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

    @PostMapping("/admin/refresh-token")
    public ResponseEntity<ApiResponse<LoginResponse>> adminRefreshToken(
            HttpServletRequest request,
            HttpServletResponse response) {

        try {
            String refreshToken = Arrays.stream(request.getCookies())
                    .filter(c -> c.getName().equals("refreshToken"))
                    .map(Cookie::getValue)
                    .findFirst()
                    .orElse(null);

            // Rolling 방식으로 refreshToken 갱신
            LoginResponse loginResponse = authService.refreshTokenWithRolling(refreshToken);
            
            // 새로운 refreshToken을 쿠키에 설정
            ResponseCookie newRefreshCookie = ResponseCookie.from("refreshToken", loginResponse.getRefreshToken())
                    .httpOnly(true)
                    .secure(false)
                    .sameSite("Strict")
                    .path("/")
                    .maxAge(Duration.ofDays(7))
                    .build();
            
            response.addHeader(HttpHeaders.SET_COOKIE, newRefreshCookie.toString());

            return ResponseUtil.success(loginResponse);

        } catch (Exception e) {
            return null;
        }
    }

    // 사용자용 (앱)
    @PostMapping("/user/login")
    public ResponseEntity<ApiResponse<LoginResponse>> userLogin(
            @RequestBody LoginRequest loginRequest) {

        LoginResponse loginResponse = authService.login(loginRequest);

        // 앱용: refreshToken을 응답에 포함
        String refreshToken = authService.createRefreshToken(loginRequest);
        loginResponse.setRefreshToken(refreshToken);

        return ResponseUtil.success(loginResponse);
    }

    @PostMapping("/user/logout")
    public ResponseEntity<ApiResponse<String>> userLogout(@RequestBody TokenRefreshRequest request) {
        // refreshToken 무효화 (DB에서 삭제)
        authService.invalidateRefreshToken(request.getRefreshToken());
        return ResponseUtil.success("로그아웃 완료");
    }

    @PostMapping("/user/refresh-token")
    public ResponseEntity<ApiResponse<LoginResponse>> userRefreshToken(@RequestBody TokenRefreshRequest request) {
        try {
            // Rolling 방식으로 refreshToken 갱신
            LoginResponse response = authService.refreshTokenWithRolling(request.getRefreshToken());
            return ResponseUtil.success(response);
        } catch (Exception e) {
            return null;
        }
    }

    @PostMapping("/user/refresh-token-renewal")
    public ResponseEntity<ApiResponse<LoginResponse>> userRefreshTokenRenewal(@RequestBody TokenRefreshRequest request) {
        try {
            // refreshToken만 갱신하여 반환
            LoginResponse response = authService.refreshTokenRenewal(request.getRefreshToken());
            return ResponseUtil.success(response);
        } catch (Exception e) {
            return null;
        }
    }

}
