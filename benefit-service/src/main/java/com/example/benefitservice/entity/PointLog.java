package com.example.benefitservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "point_log", schema = "benefit_service")
public class PointLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "customer_id", nullable = false)
    private int customerId;

    @Column(name = "type",length = 20, nullable = false)
    private String type;

    @Column(name = "store_id", nullable = false)
    private int storeId;

    @Column(name = "point_amount", nullable = false)
    private int pointAmount;

    @Lob
    @Column(name = "reason")
    private String reason;

    @Column(name = "created_at")
    @CreationTimestamp
    private LocalDateTime createdAt;

}
