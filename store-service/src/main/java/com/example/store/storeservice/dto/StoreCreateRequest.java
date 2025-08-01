package com.example.store.storeservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
public class StoreCreateRequest {

    @NotBlank(message = "매장명")
    private String name;

    @NotBlank(message = "점주명")
    private String ownerName;

    @NotBlank(message = "매장 번호")
    private String phone;

}
