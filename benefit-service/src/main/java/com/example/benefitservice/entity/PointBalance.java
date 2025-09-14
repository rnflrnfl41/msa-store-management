package com.example.benefitservice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "point_balance")
@Getter
@Setter
/**
 * Point Balance Entity
 */
public class PointBalance {

    @Id
    @Column(name = "customer_id")
    private Integer customerId;

    @Column(name = "store_id")
    private Integer storeId;

    @Column(name = "total_points")
    private Integer totalPoints;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

}