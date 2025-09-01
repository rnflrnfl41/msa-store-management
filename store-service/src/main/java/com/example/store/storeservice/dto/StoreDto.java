package com.example.store.storeservice.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
public class StoreDto {

    private int id;

    private String name;

    private String ownerName;

    private String phone;


}
