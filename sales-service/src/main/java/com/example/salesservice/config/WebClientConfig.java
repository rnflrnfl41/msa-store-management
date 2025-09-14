package com.example.salesservice.config;

import com.example.salesservice.config.props.SecurityProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

import static com.example.Constant.HttpHeaderConstants.X_GATEWAY_TOKEN;

@Configuration
@Slf4j
public class WebClientConfig {

    @Value("${external.base-url.benefit-service}")
    private String benefitServiceBaseUrl;

    @Bean
    public WebClient benefitServiceWebClient(WebClient.Builder builder, SecurityProperties securityProperties) {
        return builder
                .baseUrl(benefitServiceBaseUrl) // ex: http://localhost:8081/api/user
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE) // 기본 헤더 설정
                .defaultHeader(X_GATEWAY_TOKEN, securityProperties.getInternalToken())
                .filter(logRequest()) // 요청/응답 로깅 필터
                .build();
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
