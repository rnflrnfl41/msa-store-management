package com.example.authservice.controller;

import com.example.authservice.entity.user.Role;
import com.example.authservice.dto.LoginRequest;
import com.example.authservice.dto.LoginResponse;
import com.example.authservice.dto.SignupDto;
import com.example.authservice.dto.TokenRefreshRequest;
import com.example.authservice.service.AuthService;
import com.example.dto.ApiResponse;
import com.example.exception.CommonException;
import com.example.exception.CommonExceptionCode;
import com.example.util.AuthUtil;
import com.example.util.ResponseUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
            @RequestBody LoginRequest loginRequest) {
        LoginResponse response = authService.login(loginRequest);
        return ResponseUtil.success(response);
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
    public ResponseEntity<ApiResponse<LoginResponse>> refreshToken(@RequestBody TokenRefreshRequest request) {
        LoginResponse response = authService.refreshToken(request);
        return ResponseUtil.success(response);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<ApiResponse<String>> deleteUser(@PathVariable UUID userId,
                                           @RequestHeader(X_USER_ROLE) String role){

        AuthUtil.validateAdmin(role);
        authService.deleteUser(userId);
        return ResponseUtil.success("유저 삭제 완료");

    }

}
