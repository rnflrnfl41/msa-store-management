package com.example.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.UUID;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

    private UUID id;

    @NotBlank(message = "로그인 아이디")
    private String loginId;

    private String password;

    @NotBlank(message = "이름")
    private String name;


}
