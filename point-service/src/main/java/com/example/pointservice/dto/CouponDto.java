package com.example.pointservice.dto;

import lombok.*;

import java.time.LocalDate;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CouponDto {

    private String id;

    private String name;

    private String amount;

    private String type;

    private LocalDate createdDate;

    private LocalDate expiryDate;

    private boolean used;

    private LocalDate usedDate;


}
