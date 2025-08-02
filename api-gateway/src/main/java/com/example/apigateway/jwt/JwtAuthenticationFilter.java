package com.example.apigateway.jwt;

import com.example.apigateway.jwt.props.SecurityProperties;
import com.example.exception.CommonException;
import com.example.exception.CommonExceptionCode;
import com.example.util.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    private final JwtUtil jwtUtil;
    private final SecurityProperties securityProperties;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();


        // 1. 허용 경로는 JWT 검사 생략
        String path = request.getURI().getPath();

        log.info("Request path: {}", path); // 로그 추가

        if (isPermitPath(path)) {
            return chain.filter(exchange);
        }

        // 2. 토큰 추출
        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return onError(response, new CommonException(CommonExceptionCode.MISSING_TOKEN));
        }

        String token = authHeader.substring(7);

        // 3. 토큰 검증
        CommonException error = jwtUtil.validateToken(token);
        if (error != null) {
            return onError(response, error);
        }

        // 4. 사용자 정보 추출
        Claims claims = jwtUtil.getClaims(token);
        String userIdx = claims.getSubject();
        String loginId = (String) claims.get("loginId");
        String name = (String) claims.get("name");
        String storeId = (String) claims.get("storeId");
        String role = (String) claims.get("role");

        // 5. 요청 헤더에 사용자 정보 추가
        ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
                .header("X-User-Idx", userIdx)
                .header("X-User-LoginId", loginId)
                .header("X-User-Name", name)
                .header("X-User-StoreId", storeId)
                .header("X-User-Role", role)
                .header("X-Gateway-Token", securityProperties.getInternalToken())
                .build();

        return chain.filter(exchange.mutate().request(modifiedRequest).build());
    }

    private boolean isPermitPath(String path) {
        return securityProperties.getPermitAllPaths().stream()
                .anyMatch(p -> new AntPathMatcher().match(p, path));
    }

    private Mono<Void> onError(ServerHttpResponse response, CommonException ex) {
        response.setStatusCode(ex.getStatus());
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        String body = String.format(
                "{\"message\": \"%s\", \"code\": \"%s\", \"status\": %d, \"timestamp\": \"%s\"}",
                ex.getMessage(),
                ex.getCode().name(),
                ex.getStatus().value(),
                LocalDateTime.now()
        );
        DataBuffer buffer = response.bufferFactory().wrap(body.getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(buffer));
    }

    @Override
    public int getOrder() {
        return -1; // 우선순위 (낮을수록 먼저 실행)
    }
}

