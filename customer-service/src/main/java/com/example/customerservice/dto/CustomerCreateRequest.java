package com.example.customerservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CustomerCreateRequest {

    @NotBlank(message = "고객 이름은 필수입니다.")
    @Size(max = 50, message = "고객 이름은 50자를 초과할 수 없습니다.")
    private String name;

    @Size(max = 20, message = "전화번호는 20자를 초과할 수 없습니다.")
    @Pattern(regexp = "^01[0-9]-[0-9]{3,4}-[0-9]{4}$", message = "올바른 전화번호 형식이 아닙니다. (예: 010-1234-5678)")
    @NotBlank(message = "고객 전화번호는 필수입니다.")
    private String phone;
} 