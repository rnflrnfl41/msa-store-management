package com.example.store.storeservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Entity
@Builder
@Table(name = "store")
@AllArgsConstructor
@NoArgsConstructor
public class Store {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "public_id")
    private UUID publicId;

    @Size(max = 100)
    @NotNull
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Size(max = 50)
    @Column(name = "owner_name", length = 50)
    private String ownerName;

    @Size(max = 20)
    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "created_at")
    private Instant createdAt;

    //저장시 자동으로 UUID 입력 해줌
    //DB 에서도 gen_random_uuid() 으로 해주지만
    //안전빵으로 Jpa에서도 처리 로직 필요
    @PrePersist
    public void generateUUID() {
        if (publicId == null) {
            publicId = UUID.randomUUID();
        }
    }

}