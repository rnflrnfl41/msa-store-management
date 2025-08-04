package com.example.salesservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "payment", schema = "sales_service")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @NotNull
    @Column(name = "amount", nullable = false)
    private Integer amount;

    @Column(name = "discount")
    private Integer discount;

    @Size(max = 30)
    @Column(name = "payment_method", length = 30)
    private String paymentMethod;

    @Column(name = "points_used")
    private Integer pointsUsed;

    @NotNull
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @NotNull
    @Column(name = "visit_id", nullable = false)
    private Integer visitId;

}