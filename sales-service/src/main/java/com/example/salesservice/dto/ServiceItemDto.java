package com.example.salesservice.dto;

import lombok.*;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceItemDto {

    private int serviceId;
    private String name;
    private int price;


}
