package com.example.authservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SignupDto {

    @NotBlank(message = "아이디")
    private String loginId;

    @NotBlank(message = "비밀번호")
    private String password;

    @NotBlank(message = "이름")
    private String name;

}
