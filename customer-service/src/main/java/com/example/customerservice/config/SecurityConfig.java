package com.example.customerservice.config;

import com.example.customerservice.config.props.SecurityProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final SecurityProperties securityProperties;
    private static final String GATEWAY_TOKEN_HEADER = "X-Gateway-Token";


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().access((authentication, context) -> {
                            String token = context.getRequest().getHeader(GATEWAY_TOKEN_HEADER);
                            boolean authorized = securityProperties.getInternalToken().equals(token);
                            return new AuthorizationDecision(authorized);
                        })
                )
                .build();
    }

}
