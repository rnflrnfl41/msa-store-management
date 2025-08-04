package com.example.expenseservice.exception;

import com.example.dto.ErrorResponse;
import com.example.exception.CommonException;
import com.example.expenseservice.dto.ErrorLog;
import com.example.expenseservice.repository.ErrorLogRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.stream.Collectors;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final ErrorLogRepository errorLogRepository;
    private final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);


    /**
     *  @NotBlank(message = "아이디는 필수입니다.") 이런식으로 Dto에 적용하고
     *  controller api 파라미터 앞에 @Valid를 붙히면
     *  파라미터가 안들어왔을때 MethodArgumentNotValidException이 터진다
     *  ex.getBindingResult()로 message를 받아서 가공 및 response 내려 즘
     *  이렇게 사용하려면 org.springframework.boot:spring-boot-starter-validation
     *  의존성 필요
     * @param ex
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {

        String errorMessage = "다음 정보를 입력해주세요: " +
                ex.getBindingResult().getFieldErrors().stream()
                        .map(error -> error.getDefaultMessage())
                        .collect(Collectors.joining(", "));

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)  // 또는 적절한 상태 코드
                .body(new ErrorResponse(
                        errorMessage,
                        "INVALID_PARAMETER",
                        400,  // 또는 적절한 상태 코드
                        LocalDateTime.now()
                ));

    }


    @ExceptionHandler(CommonException.class)
    public ResponseEntity<ErrorResponse> handleCommonException(CommonException ex) {
        return ResponseEntity
                .status(ex.getStatus())
                .body(new ErrorResponse(ex));
    }


    @ExceptionHandler(Throwable.class)
    public ResponseEntity<ErrorResponse> handleThrowable(Throwable ex, HttpServletRequest request) {

        String errorId = UUID.randomUUID().toString();

        log.error("[{}] Internal server error at {} {}", errorId, request.getMethod(), request.getRequestURI(), ex);

        errorLogRepository.save(ErrorLog.builder()
                .errorId(errorId)
                .message(ex.getMessage())
                .code("INTERNAL_SERVER_ERROR")
                .status(500)
                .uri(request.getRequestURI())
                .method(request.getMethod())
                .stackTrace(ExceptionUtils.getStackTrace(ex))
                .build());

        return ResponseEntity.status(500)
                .body(new ErrorResponse(
                        "서버 내부 오류가 발생했습니다. (에러 ID: " + errorId + ")",
                        "INTERNAL_SERVER_ERROR",
                        500,
                        LocalDateTime.now()
                ));
    }

}
