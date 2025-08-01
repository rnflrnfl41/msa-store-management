package com.example.authservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TokenRefreshRequest {

    private String refreshToken;
}
