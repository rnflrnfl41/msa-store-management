package com.example.authservice.service;

import com.example.authservice.dto.SignupDto;
import com.example.util.JwtUtil;
import com.example.authservice.dto.LoginRequest;
import com.example.authservice.dto.LoginResponse;
import com.example.authservice.entity.UserInfo;
import com.example.exception.CommonException;
import com.example.exception.CommonExceptionCode;
import com.example.authservice.repository.UserInfoRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserInfoRepository userInfoRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final ModelMapper modelMapper;

    public LoginResponse login(LoginRequest loginRequest) {
        String loginId = loginRequest.getLoginId();
        String password = loginRequest.getPassword();

        UserInfo userInfo = userInfoRepository.findByLoginId(loginId)
                .orElseThrow(() -> new CommonException(CommonExceptionCode.USER_NOT_FOUND));

        if (!passwordEncoder.matches(password, userInfo.getPassword())) {
            throw new CommonException(CommonExceptionCode.ID_PASSWORD_FAIL);
        }

        Map<String, Object> claims = Map.of(
                "loginId", userInfo.getLoginId(),
                "name", userInfo.getName()
        );

        String token = jwtUtil.createToken(String.valueOf(userInfo.getIdx()), claims);

        LoginResponse response =
                LoginResponse.builder()
                        .userIdx(userInfo.getIdx())
                        .userId(loginId)
                        .userName(userInfo.getName())
                        .token(token)
                        .build();

        return response;
    }

    public void signup(SignupDto signupDto) {

        userInfoRepository.findByLoginId(signupDto.getLoginId()).ifPresent(user -> {
            throw new CommonException(CommonExceptionCode.DUPLICATE_LOGIN_ID);
        });

        signupDto.setPassword(passwordEncoder.encode(signupDto.getPassword()));
        UserInfo userInfo = modelMapper.map(signupDto,UserInfo.class);
        userInfoRepository.save(userInfo);
    }
}
