package com.example.authservice.service;

import com.example.authservice.dto.*;
import com.example.authservice.entity.RefreshToken;
import com.example.authservice.entity.user.Role;
import com.example.authservice.entity.user.User;
import com.example.authservice.repository.RefreshTokenRepository;
import com.example.authservice.repository.UserRepository;
import com.example.util.JwtUtil;
import com.example.exception.CommonException;
import com.example.exception.CommonExceptionCode;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;

    public LoginResponse login(LoginRequest loginRequest) {
        String loginId = loginRequest.getLoginId();
        String password = loginRequest.getPassword();

        User user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new CommonException(CommonExceptionCode.USER_NOT_FOUND));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new CommonException(CommonExceptionCode.ID_PASSWORD_FAIL);
        }

        Map<String, Object> claims = Map.of(
                "storeId", user.getStoreId(),
                "loginId", user.getLoginId(),
                "name", user.getName(),
                "role", user.getRole()
        );

        String token = jwtUtil.createToken(String.valueOf(user.getId()), claims);

        LoginResponse response =
                LoginResponse.builder()
                        .userId(user.getId())
                        .loginId(loginId)
                        .storeId(user.getStoreId())
                        .userName(user.getName())
                        .accessToken(token)
                        .build();

        return response;
    }

    @Transactional
    public ResponseCookie setRefreshToken(LoginRequest loginRequest){

        String loginId = loginRequest.getLoginId();

        User user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new CommonException(CommonExceptionCode.USER_NOT_FOUND));

        String refreshToken = jwtUtil.createRefreshToken(String.valueOf(user.getId()));

        refreshTokenRepository.deleteByUser_Id(user.getId());

        RefreshToken tokenEntity = RefreshToken.builder()
                .user(user)
                .token(refreshToken)
                .expiredAt(jwtUtil.getRefreshTokenExpiration(refreshToken))
                .build();

        refreshTokenRepository.save(tokenEntity);

        return ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(false) // HTTPS 환경에서만 적용 (일단 개발용으로 false)
                .sameSite("Strict")
                .path("/") // 경로 전체에서 사용
                .maxAge(Duration.ofDays(7)) // 유효기간 설정
                .build();

    }

    @Transactional
    public String createRefreshToken(LoginRequest loginRequest) {
        String loginId = loginRequest.getLoginId();
        User user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new CommonException(CommonExceptionCode.USER_NOT_FOUND));
        
        String refreshToken = jwtUtil.createRefreshToken(String.valueOf(user.getId()));
        saveRefreshToken(user.getId(), refreshToken);
        
        return refreshToken;
    }

    @Transactional
    public void saveRefreshToken(UUID userId, String refreshToken) {
        refreshTokenRepository.deleteByUser_Id(userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CommonException(CommonExceptionCode.USER_NOT_FOUND));

        RefreshToken tokenEntity = RefreshToken.builder()
                .user(user)
                .token(refreshToken)
                .expiredAt(jwtUtil.getRefreshTokenExpiration(refreshToken))
                .build();

        refreshTokenRepository.save(tokenEntity);
    }

    public LoginResponse refreshToken(String refreshToken) {

        RefreshToken storedToken = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new CommonException(CommonExceptionCode.SAVED_REFRESH_TOKEN_NOT_EXIST));

        User user = storedToken.getUser();

        if (storedToken.getExpiredAt().isBefore(Instant.now())) {
            throw new CommonException(CommonExceptionCode.EXPIRED_REFRESH_TOKEN);
        }

        // 새로운 accessToken 생성
        Map<String, Object> newClaims = Map.of(
                "storeId", user.getStoreId(),
                "loginId", user.getLoginId(),
                "name", user.getName(),
                "role", user.getRole()
        );

        String newAccessToken = jwtUtil.createToken(String.valueOf(user.getId()), newClaims);

        // 새로운 refreshToken 생성 (Rolling 방식)
        String newRefreshToken = jwtUtil.createRefreshToken(String.valueOf(user.getId()));
        
        // 기존 refreshToken 삭제 후 새로운 것 저장
        refreshTokenRepository.delete(storedToken);
        saveRefreshToken(user.getId(), newRefreshToken);

        LoginResponse response = LoginResponse.builder()
                .userId(user.getId())
                .loginId(user.getLoginId())
                .storeId(user.getStoreId())
                .userName(user.getName())
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken) // 새로운 refreshToken 포함
                .build();

        return response;

    }

    // Rolling Refresh Token 방식으로 refreshToken 갱신
    @Transactional
    public LoginResponse refreshTokenWithRolling(String refreshToken) {
        RefreshToken storedToken = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new CommonException(CommonExceptionCode.SAVED_REFRESH_TOKEN_NOT_EXIST));

        User user = storedToken.getUser();

        if (storedToken.getExpiredAt().isBefore(Instant.now())) {
            throw new CommonException(CommonExceptionCode.EXPIRED_REFRESH_TOKEN);
        }

        // 새로운 accessToken 생성
        Map<String, Object> newClaims = Map.of(
                "storeId", user.getStoreId(),
                "loginId", user.getLoginId(),
                "name", user.getName(),
                "role", user.getRole()
        );

        String newAccessToken = jwtUtil.createToken(String.valueOf(user.getId()), newClaims);

        // 새로운 refreshToken 생성 (Rolling 방식)
        String newRefreshToken = jwtUtil.createRefreshToken(String.valueOf(user.getId()));
        
        // 기존 refreshToken 삭제 후 새로운 것 저장
        refreshTokenRepository.delete(storedToken);
        saveRefreshToken(user.getId(), newRefreshToken);

        LoginResponse response = LoginResponse.builder()
                .userId(user.getId())
                .loginId(user.getLoginId())
                .storeId(user.getStoreId())
                .userName(user.getName())
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken) // 새로운 refreshToken 포함
                .build();

        return response;
    }

    // refreshToken 갱신용: 새로운 refreshToken 생성, 기존 refreshToken 무효화
    @Transactional
    public LoginResponse refreshTokenRenewal(String refreshToken) {
        RefreshToken storedToken = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new CommonException(CommonExceptionCode.SAVED_REFRESH_TOKEN_NOT_EXIST));

        User user = storedToken.getUser();

        if (storedToken.getExpiredAt().isBefore(Instant.now())) {
            throw new CommonException(CommonExceptionCode.EXPIRED_REFRESH_TOKEN);
        }

        // 새로운 refreshToken 생성
        String newRefreshToken = jwtUtil.createRefreshToken(String.valueOf(user.getId()));
        
        // 기존 refreshToken 삭제 후 새로운 것 저장
        refreshTokenRepository.delete(storedToken);
        saveRefreshToken(user.getId(), newRefreshToken);

        return LoginResponse.builder()
            .refreshToken(newRefreshToken) // 새로운 refreshToken만 설정
            .build();
    }

    @Transactional
    public void invalidateRefreshToken(String refreshToken) {
        RefreshToken token = refreshTokenRepository.findByToken(refreshToken)
                .orElse(null);
        if (token != null) {
            refreshTokenRepository.delete(token);
        }
    }

}
