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
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final ModelMapper modelMapper;
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
                        .token(token)
                        .build();

        return response;
    }

    public ResponseCookie setRefreshToken(LoginRequest loginRequest){

        String loginId = loginRequest.getLoginId();

        User user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new CommonException(CommonExceptionCode.USER_NOT_FOUND));

        // 디버깅 코드 추가
        System.out.println("User ID: " + user.getId());
        System.out.println("User ID 타입: " + user.getId().getClass().getName());
        System.out.println("User ID toString: " + user.getId().toString());

        boolean exists = userRepository.existsById(user.getId());
        System.out.println("DB에 사용자 존재 여부: " + exists);

        String refreshToken = jwtUtil.createRefreshToken(String.valueOf(user.getId()));

        refreshTokenRepository.deleteByUser_Id(user.getId());

        RefreshToken tokenEntity = RefreshToken.builder()
                .user(user)
                .token(refreshToken)
                .expiredAt(jwtUtil.getRefreshTokenExpiration(refreshToken))
                .build();

        System.out.println("저장하려는 RefreshToken의 User ID: " + tokenEntity.getUser().getId());

        refreshTokenRepository.save(tokenEntity);

        return ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(false) // HTTPS 환경에서만 적용 (일단 개발용으로 false)
                .sameSite("Strict")
                .path("/") // 경로 전체에서 사용
                .maxAge(Duration.ofDays(7)) // 유효기간 설정
                .build();

    }

    public void signup(SignupDto signupDto) {

        userRepository.findByLoginId(signupDto.getLoginId()).ifPresent(user -> {
            throw new CommonException(CommonExceptionCode.DUPLICATE_LOGIN_ID);
        });

        signupDto.setPassword(passwordEncoder.encode(signupDto.getPassword()));
        User user = modelMapper.map(signupDto,User.class);
        userRepository.save(user);
    }

    public LoginResponse refreshToken(HttpServletRequest request) {

        String refreshToken = Arrays.stream(request.getCookies())
                .filter(c -> c.getName().equals("refreshToken"))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);

        if(refreshToken == null || refreshToken.isEmpty()){
            throw new CommonException(CommonExceptionCode.MISSING_REFRESH_TOKEN);
        }

        RefreshToken storedToken = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new CommonException(CommonExceptionCode.SAVED_REFRESH_TOKEN_NOT_EXIST));

        User user = storedToken.getUser();

        if (storedToken.getExpiredAt().isBefore(Instant.now())) {
            throw new CommonException(CommonExceptionCode.EXPIRED_REFRESH_TOKEN);
        }

        Map<String, Object> newClaims = Map.of(
                "storeId", user.getStoreId(),
                "loginId", user.getLoginId(),
                "name", user.getName(),
                "role", user.getRole()
        );

        String newAccessToken = jwtUtil.createToken(String.valueOf(user.getId()), newClaims);



        LoginResponse response = LoginResponse.builder()
                .userId(user.getId())
                .loginId(user.getLoginId())
                .storeId(user.getStoreId())
                .userName(user.getName())
                .token(newAccessToken)
                .build();

        return response;

    }

    public void deleteUser(UUID userId) {

        userRepository.deleteById(userId);

    }

    public List<UserDto> getAllUserInfoByStoreId(UUID storeId) {

        List<User> userList = userRepository.findByStoreId(storeId);

        return userList.stream()
                .map(user -> modelMapper.map(user,UserDto.class))
                .toList();

    }

    public void updateUser(UUID userId, UserDto userDto) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CommonException(CommonExceptionCode.USER_NOT_FOUND));

        String encodedPassword = passwordEncoder.encode(userDto.getPassword());

        user.setLoginId(userDto.getLoginId());
        user.setName(userDto.getName());
        user.setPassword(encodedPassword);

        userRepository.save(user);

    }
}
