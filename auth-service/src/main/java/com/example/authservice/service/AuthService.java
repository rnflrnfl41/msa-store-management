package com.example.authservice.service;

import com.example.authservice.dto.LoginRequest;
import com.example.authservice.dto.LoginResponse;
import com.example.authservice.entity.UserInfo;
import com.example.authservice.exception.CommonException;
import com.example.authservice.exception.CommonExceptionCode;
import com.example.authservice.repository.UserInfoRepository;
import com.example.authservice.util.ApiResponse;
import com.example.authservice.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserInfoRepository userInfoRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public LoginResponse login(LoginRequest loginRequest) {
        String loginId = loginRequest.getLoginId();
        String password = loginRequest.getPassword();

        UserInfo userInfo = userInfoRepository.findByLoginId(loginId)
                .orElseThrow(() -> new CommonException(CommonExceptionCode.USER_NOT_FOUND));

        if (!passwordEncoder.matches(password, userInfo.getPassword())) {
            throw new CommonException(CommonExceptionCode.ID_PASSWORD_FAIL);
        }

        String token = jwtUtil.createToken(userInfo);


        LoginResponse response =
                LoginResponse.builder()
                        .userIdx(userInfo.getIdx())
                        .userId(loginId)
                        .userName(userInfo.getName())
                        .token(token)
                        .build();

        return response;
    }

}
