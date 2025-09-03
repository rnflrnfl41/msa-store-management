package com.example.pointservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "point_balance")
/**
 * 포인트 잔액
 */
public class PointBalance {

    @Id
    @Column(name = "customer_id")
    private int customerId;

    @Column(name = "store_id", nullable = false)
    private int storeId;

    @Column(name = "total_points", nullable = false)
    private int totalPoints = 0;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public void addPoints(int points) {
        this.totalPoints += points;
    }

    public void usePoints(int points) {
        this.totalPoints -= points;
    }
}
