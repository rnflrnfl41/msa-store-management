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

import static com.example.Constant.HttpHeaderConstants.*;

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

        String path = request.getURI().getPath();
        long start = System.currentTimeMillis();

        log.info("[REQUEST] Method: {}, Path: {}, Query: {}",
                request.getMethod(), path, request.getQueryParams());

        // 1. 허용된 경로는 필터 패스
        if (isPermitPath(path)) {
            return chain.filter(exchange)
                    .doOnSuccess(aVoid -> logElapsed(path, start, true))
                    .doOnError(error -> logElapsed(path, start, false, error));
        }

        // 2. Authorization 헤더에서 JWT 추출
        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return onError(response, CommonExceptionCode.MISSING_TOKEN, start, path);
        }

        String token = authHeader.substring(7);

        // 3. 토큰 유효성 검사
        CommonException error = jwtUtil.validateToken(token);
        if (error != null) {
            return onError(response, error.getCode(), start, path);
        }

        // 4. 토큰에서 사용자 정보 추출
        Claims claims = jwtUtil.getClaims(token);
        String userIdx = claims.getSubject();
        String loginId = (String) claims.get("loginId");
        String name = (String) claims.get("name");
        String storeId = (String) claims.get("storeId");
        String role = (String) claims.get("role");

        // 5. 사용자 정보를 헤더에 추가
        ServerHttpRequest modifiedRequest = request.mutate()
                .header(X_USER_IDX, userIdx)
                .header(X_USER_LOGIN_ID, loginId)
                .header(X_USER_NAME, name)
                .header(X_USER_STORE_ID, storeId)
                .header(X_USER_ROLE, role)
                .header(X_GATEWAY_TOKEN, securityProperties.getInternalToken())
                .build();

        ServerWebExchange modifiedExchange = exchange.mutate().request(modifiedRequest).build();

        // 6. 필터 체인 실행 및 응답 로깅
        return chain.filter(modifiedExchange)
                .doOnSuccess(aVoid -> logElapsed(path, start, true))
                .doOnError(errorEx -> logElapsed(path, start, false, errorEx));
    }

    private boolean isPermitPath(String path) {
        return securityProperties.getPermitAllPaths().stream()
                .anyMatch(p -> new AntPathMatcher().match(p, path));
    }

    private void logElapsed(String path, long startTime, boolean success) {
        long elapsed = System.currentTimeMillis() - startTime;
        if (success) {
            log.info("[RESPONSE SUCCESS] Path: {}, Elapsed: {}ms", path, elapsed);
        } else {
            log.warn("[RESPONSE FAILED] Path: {}, Elapsed: {}ms", path, elapsed);
        }
    }

    private void logElapsed(String path, long startTime, boolean success, Throwable error) {
        long elapsed = System.currentTimeMillis() - startTime;
        if (success) {
            log.info("[RESPONSE SUCCESS] Path: {}, Elapsed: {}ms", path, elapsed);
        } else {
            log.error("[RESPONSE ERROR] Path: {}, Error: {}, Elapsed: {}ms", path, error.getMessage(), elapsed);
        }
    }

    private Mono<Void> onError(ServerHttpResponse response, CommonExceptionCode code, long start, String path) {
        long elapsed = System.currentTimeMillis() - start;

        response.setStatusCode(code.getStatus());
        response.getHeaders().setContentType(MediaType.valueOf("application/json;charset=UTF-8"));
        response.getHeaders().add("Cache-Control", "no-store");
        response.getHeaders().add("Pragma", "no-cache");

        String body = String.format(
                "{\"message\": \"%s\", \"code\": \"%s\", \"status\": %d, \"timestamp\": \"%s\"}",
                code.getMessage(),
                code.name(),
                code.getStatus().value(),
                LocalDateTime.now()
        );

        DataBuffer buffer = response.bufferFactory().wrap(body.getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(buffer))
                .doOnTerminate(() -> log.error("[RESPONSE ERROR] Path: {}, Status: {}, Elapsed: {}ms", path, code.getStatus(), elapsed));
    }

    @Override
    public int getOrder() {
        return -1; // 높은 우선순위 (낮을수록 먼저 실행)
    }
}


