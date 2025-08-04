package com.example.expenseservice.dto;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "expense")
public class Expense {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @NotNull
    @Column(name = "store_id", nullable = false)
    private Integer storeId;

    @NotNull
    @Column(name = "amount", nullable = false)
    private Integer amount;

    @Column(name = "description", length = Integer.MAX_VALUE)
    private String description;

    @NotNull
    @Column(name = "spent_at", nullable = false)
    private LocalDate spentAt;

    @Column(name = "created_at")
    private Instant createdAt;

}