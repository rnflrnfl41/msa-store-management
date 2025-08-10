package com.example.authservice.controller;

import com.example.authservice.dto.*;
import com.example.authservice.entity.user.Role;
import com.example.authservice.service.AuthService;
import com.example.dto.ApiResponse;
import com.example.exception.CommonException;
import com.example.exception.CommonExceptionCode;
import com.example.util.AuthUtil;
import com.example.util.ResponseUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static com.example.Constant.HttpHeaderConstants.X_USER_ROLE;
import static com.example.Constant.RoleConstants.ROLE_ADMIN;

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

        Cookie[] cookies = request.getCookies();

        if (cookies == null) {
            throw new CommonException(CommonExceptionCode.NO_COOKIES);
        }

        Cookie refreshToken = Arrays.stream(request.getCookies())
                .filter(c -> c.getName().equals("refreshToken"))
                .findFirst()
                .orElse(null);

        // RefreshToken 쿠키 제거
        refreshToken.setMaxAge(0);
        refreshToken.setPath("/");
        refreshToken.setHttpOnly(true);
        refreshToken.setSecure(false); // HTTPS 환경에서
        response.addCookie(refreshToken);

        return ResponseUtil.success("로그아웃 완료");
    }

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<String>> signup(@Valid @RequestBody SignupDto signupDto,
                                                      @RequestHeader(X_USER_ROLE) String role) {

        //관리자 계정만 접근 가능
        AuthUtil.validateAdmin(role);

        authService.signup(signupDto);
        return ResponseUtil.created("회원가입 완료");
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

    @GetMapping("{storeId}")
    public ResponseEntity<ApiResponse<List<UserDto>>> getAllUserInfoByStoreId(@PathVariable UUID storeId,
                                                                              @RequestHeader(X_USER_ROLE) String role) {
        AuthUtil.validateAdmin(role);
        return ResponseUtil.success(authService.getAllUserInfoByStoreId(storeId));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<ApiResponse<String>> deleteUser(@PathVariable UUID userId,
                                                          @RequestHeader(X_USER_ROLE) String role) {

        AuthUtil.validateAdmin(role);
        authService.deleteUser(userId);
        return ResponseUtil.success("유저 삭제 완료");

    }

    @PatchMapping("/{userId}")
    public ResponseEntity<ApiResponse<String>> updateUser(@PathVariable UUID userId,
                                                          @RequestBody UserDto userDto,
                                                          @RequestHeader(X_USER_ROLE) String role) {

        AuthUtil.validateAdmin(role);
        authService.updateUser(userId, userDto);
        return ResponseUtil.success("유저 수정 완료");

    }

}
