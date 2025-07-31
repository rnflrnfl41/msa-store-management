package com.example.util;

import com.example.exception.CommonException;
import com.example.exception.CommonExceptionCode;
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

    public CommonException validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(decodedSecretKey).parseClaimsJws(token);
            return null;
        } catch (ExpiredJwtException e) {
            return new CommonException(CommonExceptionCode.TOKEN_EXPIRED);
        } catch (UnsupportedJwtException | MalformedJwtException | SignatureException | IllegalArgumentException e) {
            return new CommonException(CommonExceptionCode.INVALID_TOKEN);
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
