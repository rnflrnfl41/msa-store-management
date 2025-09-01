package com.example.authservice.entity.user;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Type;

import java.sql.Types;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    @JdbcTypeCode(Types.CHAR) //JPA가 UUID를 바이트 배열이 아닌, 36자리의 문자열로 처리
    private UUID id;

    @NotNull
    @Column(name = "store_id", nullable = false)
    private Integer storeId;

    @Size(max = 50)
    @NotNull
    @Column(name = "login_id", nullable = false, length = 50)
    private String loginId;

    @NotNull
    @Column(name = "password", nullable = false, columnDefinition = "TEXT")
    private String password;

    @Size(max = 50)
    @Column(name = "name", length = 50)
    private String name;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 10)
    private Role role;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

}