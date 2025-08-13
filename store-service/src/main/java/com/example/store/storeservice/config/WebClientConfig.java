package com.example.store.storeservice.config;

import com.example.dto.ErrorResponse;
import com.example.exception.CommonException;
import com.example.exception.CommonExceptionCode;
import com.example.store.storeservice.config.props.SecurityProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

import static com.example.Constant.HttpHeaderConstants.X_GATEWAY_TOKEN;

@Configuration
@Slf4j
public class WebClientConfig {

    @Value("${external.base-url.user-service}")
    private String userServiceBaseUrl;

    @Bean
    public WebClient userServiceWebClient(WebClient.Builder builder, SecurityProperties securityProperties) {
        return builder
                .baseUrl(userServiceBaseUrl) // ex: http://localhost:8081/api/user
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE) // 기본 헤더 설정
                .defaultHeader(X_GATEWAY_TOKEN, securityProperties.getInternalToken()) // 요청/응답 로깅 필터
                .filter(logRequest())
                .filter(handleErrorResponse())
                .build();
    }

    private ExchangeFilterFunction handleErrorResponse() {
        return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
            if (clientResponse.statusCode().is4xxClientError() || clientResponse.statusCode().is5xxServerError()) {
                int statusCode = clientResponse.statusCode().value();
                return clientResponse.bodyToMono(ErrorResponse.class)
                        .defaultIfEmpty(new ErrorResponse(
                                "외부 서비스 오류",
                                clientResponse.statusCode().is4xxClientError() ? "USER_SERVICE_4XX" : "USER_SERVICE_5XX",
                                statusCode,
                                LocalDateTime.now()
                        ))
                        .flatMap(err -> {
                            CommonExceptionCode enumCode = CommonExceptionCode.valueOf(err.getCode());
                            return Mono.error(enumCode != null
                                    ? new CommonException(enumCode)
                                    : new CommonException(HttpStatus.valueOf(err.getStatus()), err.getCode(), err.getMessage()));
                        });
            }
            return Mono.just(clientResponse);
        });
    }


    private ExchangeFilterFunction logRequest() {
        return (request, next) -> {
            StringBuilder sb = new StringBuilder();
            sb.append("\n").append("Request: ").append(request.method()).append(" ").append(request.url()).append("\n");
            request.headers().forEach((name, values) ->
                    values.forEach(value -> sb.append(name).append(": ").append(value).append("\n"))
            );
            log.info(sb.toString());
            return next.exchange(request);
        };
    }

}
