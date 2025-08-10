package com.example.customerservice.dto;

import com.example.customerservice.entity.Customer;
import lombok.*;

import java.time.format.DateTimeFormatter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CustomerResponse {

    private Integer id;
    private String publicId;
    private String name;
    private String phone;
    private String createdAt; // LocalDateTime → String 변환
    private Integer storeId;

    // Entity에서 Response로 변환하는 정적 메서드
    public static CustomerResponse from(Customer customer) {
        return new CustomerResponse(
                customer.getId(),
                customer.getPublicId(),
                customer.getName(),
                customer.getPhone(),
                customer.getCreatedAt() != null ? 
                    customer.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : null,
                customer.getStoreId()
        );
    }
} 