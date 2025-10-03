package com.example.expenseservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;

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

    @Column(name = "category_name")
    private String categoryName;

    @NotNull
    @Column(name = "amount", nullable = false)
    private Integer amount;

    @Column(name = "memo", length = Integer.MAX_VALUE)
    private String memo;

    @NotNull
    @Column(name = "expense_date", nullable = false)
    private LocalDate expenseDate;

    @Column(name = "created_at")
    @CreationTimestamp
    private LocalDateTime createdAt;

}