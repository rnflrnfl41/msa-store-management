package com.example;

import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;

import java.util.Base64;
import java.util.Date;
import java.util.Map;

@Slf4j
public class JwtUtil {

    private final byte[] decodedSecretKey;
    private static final long TOKEN_VALID_TIME = 8 * 60 * 60 * 1000L;

    public JwtUtil(String base64EncodedSecretKey) {
        this.decodedSecretKey = Base64.getDecoder().decode(base64EncodedSecretKey);
    }

    public String createToken(String subject, Map<String, Object> claims) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + TOKEN_VALID_TIME);

        return Jwts.builder()
                .setSubject(subject)
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(SignatureAlgorithm.HS256, decodedSecretKey)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(decodedSecretKey).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.warn("Invalid JWT Token", e);
            return false;
        }
    }

    public Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(decodedSecretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

}
