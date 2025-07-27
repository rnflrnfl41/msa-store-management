package com.example.authservice.util;


import com.example.authservice.entity.UserInfo;
import io.jsonwebtoken.*;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.Date;

@Slf4j
@Component
public class JwtUtil {

    @Value("${jwt.token.key}")
    private String secretKey;
    private byte[] decodedSecretKey;

    private static final long TOKEN_VALID_TIME = 8 * 60 * 60 * 1000L;

    @PostConstruct
    protected void init() {
        decodedSecretKey = Base64.getDecoder().decode(secretKey);
    }

    // 토큰 생성
    public String createToken(UserInfo userInfo) {
        Claims claims = Jwts.claims().setSubject(String.valueOf(userInfo.getIdx()));
        claims.put("loginId", userInfo.getLoginId());
        claims.put("name", userInfo.getName());

        Date now = new Date();

        // 8시간
        Date validity = new Date(now.getTime() + TOKEN_VALID_TIME);

        // Base64로 디코딩된 secretKey 사용
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity) // 유효시간
                .signWith(SignatureAlgorithm.HS256, decodedSecretKey)
                .compact();
    }

    // 토큰 유효성 검사
    public boolean validateToken(String token) {

        try {
            Jwts.parserBuilder().setSigningKey(decodedSecretKey).build().parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("Invalid JWT Token", e);
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT Token", e);
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT Token", e);
        } catch (IllegalArgumentException e) {
            log.info("JWT claims string is empty.", e);
        }
        return false;

    }

}
