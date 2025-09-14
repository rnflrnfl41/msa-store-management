package com.example.benefitservice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "coupon")
public class Coupon {
    
    @Id
    @Column(name = "id", length = 36)
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;

    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @Column(name = "amount", nullable = false)
    private int amount;

    @Column(name = "type", length = 30, nullable = false)
    private String type;

    @Column(name = "is_used")
    private Boolean isUsed;

    @CreationTimestamp
    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdDate;

    @Column(name = "used_date")
    private LocalDate usedDate;

    @Column(name = "expiry_date")
    private LocalDate expiryDate;

    @Column(name = "customer_id", nullable = false)
    private int customerId;

    @Column(name = "store_id", nullable = false)
    private int storeId;
}
