package com.example.authservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.example.util.JwtUtil;

@Configuration
public class JwtConfig {

    @Value("${jwt.token.key}")
    private String jwtKey;

    @Bean
    public JwtUtil jwtUtil() {
        return new JwtUtil(jwtKey);
    }
}
