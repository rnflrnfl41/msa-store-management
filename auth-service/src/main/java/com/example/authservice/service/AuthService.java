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
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
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
        String refreshToken = jwtUtil.createRefreshToken(String.valueOf(user.getId()));

        refreshTokenRepository.deleteByUser_Id(user.getId());
        refreshTokenRepository.save(
                RefreshToken.builder()
                        .user(user)
                        .token(refreshToken)
                        .expiredAt(jwtUtil.getRefreshTokenExpiration(refreshToken))
                        .build());

        LoginResponse response =
                LoginResponse.builder()
                        .userId(user.getId())
                        .loginId(loginId)
                        .storeId(user.getStoreId())
                        .userName(user.getName())
                        .token(token)
                        .refreshToken(refreshToken)
                        .build();

        return response;
    }

    public void signup(SignupDto signupDto) {

        userRepository.findByLoginId(signupDto.getLoginId()).ifPresent(user -> {
            throw new CommonException(CommonExceptionCode.DUPLICATE_LOGIN_ID);
        });

        signupDto.setPassword(passwordEncoder.encode(signupDto.getPassword()));
        User user = modelMapper.map(signupDto,User.class);
        userRepository.save(user);
    }

    public LoginResponse refreshToken(TokenRefreshRequest request) {

        String refreshToken = request.getRefreshToken();

        if(refreshToken == null || refreshToken.isEmpty()){
            throw new CommonException(CommonExceptionCode.MISSING_REFRESH_TOKEN);
        }

        UUID userId = jwtUtil.getSubjects(refreshToken);

        RefreshToken storedToken = refreshTokenRepository.findByUser_Id(userId)
                .orElseThrow(() -> new CommonException(CommonExceptionCode.SAVED_REFRESH_TOKEN_NOT_EXIST));

        if (!storedToken.getToken().equals(refreshToken)) {
            throw new CommonException(CommonExceptionCode.INVALID_REFRESH_TOKEN);
        }

        if (storedToken.getExpiredAt().isBefore(Instant.now())) {
            throw new CommonException(CommonExceptionCode.EXPIRED_REFRESH_TOKEN);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CommonException(CommonExceptionCode.USER_NOT_FOUND));

        Map<String, Object> newClaims = Map.of(
                "storeId", user.getStoreId(),
                "loginId", user.getLoginId(),
                "name", user.getName(),
                "role", user.getRole()
        );

        String newAccessToken = jwtUtil.createToken(String.valueOf(userId), newClaims);

        LoginResponse response = LoginResponse.builder()
                .userId(userId)
                .loginId(user.getLoginId())
                .storeId(user.getStoreId())
                .userName(user.getName())
                .token(newAccessToken)
                .refreshToken(refreshToken)
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
