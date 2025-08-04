package com.example.store.storeservice.config;

import com.example.store.storeservice.config.props.SecurityProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

import static com.example.Constant.HttpHeaderConstants.X_GATEWAY_TOKEN;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final SecurityProperties securityProperties;


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().access((authentication, context) -> {
                            String token = context.getRequest().getHeader(X_GATEWAY_TOKEN);
                            boolean authorized = securityProperties.getInternalToken().equals(token);
                            return new AuthorizationDecision(authorized);
                        })
                )
                .build();
    }

}
