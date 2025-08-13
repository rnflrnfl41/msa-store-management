package com.example.authservice.controller;

import com.example.authservice.dto.SignupDto;
import com.example.authservice.dto.UserDto;
import com.example.authservice.service.UserService;
import com.example.dto.ApiResponse;
import com.example.util.AuthUtil;
import com.example.util.ResponseUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static com.example.Constant.HttpHeaderConstants.X_USER_ROLE;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<ApiResponse<String>> createUser(@Valid @RequestBody SignupDto signupDto,
                                                          @RequestHeader(X_USER_ROLE) String role) {

        //관리자 계정만 접근 가능
        AuthUtil.validateAdmin(role);

        userService.createUser(signupDto);
        return ResponseUtil.created("회원가입 완료");
    }

    @GetMapping("/{storeId}")
    public ResponseEntity<ApiResponse<List<UserDto>>> getAllUserInfoByStoreId(@PathVariable UUID storeId,
                                                                              @RequestHeader(X_USER_ROLE) String role) {
        AuthUtil.validateAdmin(role);
        return ResponseUtil.success(userService.getAllUserInfoByStoreId(storeId));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<ApiResponse<String>> deleteUser(@PathVariable UUID userId,
                                                          @RequestHeader(X_USER_ROLE) String role) {

        AuthUtil.validateAdmin(role);
        userService.deleteUser(userId);
        return ResponseUtil.success("유저 삭제 완료");

    }

    @DeleteMapping("/store/{storeId}")
    public ResponseEntity<ApiResponse<String>> deleteUserByStoreId(@PathVariable UUID storeId,
                                                          @RequestHeader(X_USER_ROLE) String role) {

        AuthUtil.validateAdmin(role);
        userService.deleteUserByStoreId(storeId);
        return ResponseUtil.success("store 전체 유저 삭제 완료");

    }

    @PatchMapping("/{userId}")
    public ResponseEntity<ApiResponse<String>> updateUser(@PathVariable UUID userId,
                                                          @RequestBody UserDto userDto,
                                                          @RequestHeader(X_USER_ROLE) String role) {

        AuthUtil.validateAdmin(role);
        userService.updateUser(userId, userDto);
        return ResponseUtil.success("유저 수정 완료");

    }

}
