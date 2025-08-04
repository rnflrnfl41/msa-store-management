package com.example.pointservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "point", schema = "point_service")
public class Point {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @NotNull
    @Column(name = "customer_id", nullable = false)
    private Integer customerId;

    @NotNull
    @Column(name = "store_id", nullable = false)
    private Integer storeId;

    @NotNull
    @Column(name = "point_amount", nullable = false)
    private Integer pointAmount;

    @Lob
    @Column(name = "reason")
    private String reason;

    @Column(name = "created_at")
    private Instant createdAt;

}