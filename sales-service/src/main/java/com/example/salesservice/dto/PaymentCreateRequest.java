package com.example.salesservice.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentCreateRequest {

    @NotNull(message = "결제 금액은 필수입니다.")
    @Positive(message = "결제 금액은 0보다 커야 합니다.")
    private Integer amount;

    @Min(value = 0, message = "할인 금액은 0 이상이어야 합니다.")
    private Integer discount;

    @Size(max = 30, message = "결제 방법은 30자를 초과할 수 없습니다.")
    private String paymentMethod;

    @Min(value = 0, message = "사용 포인트는 0 이상이어야 합니다.")
    private Integer pointsUsed;

    @NotNull(message = "방문 ID는 필수입니다.")
    private Integer visitId;
} 