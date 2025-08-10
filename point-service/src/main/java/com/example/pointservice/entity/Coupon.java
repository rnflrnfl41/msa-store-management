package com.example.pointservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.JdbcTypeCode;

import java.sql.Types;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "coupon", schema = "point_service")
public class Coupon {
    @Id
    @Column(name = "id", nullable = false, length = 36)
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @JdbcTypeCode(Types.CHAR) //JPA가 UUID를 바이트 배열이 아닌, 36자리의 문자열로 처리
    private UUID id;

    @Size(max = 30)
    @Column(name = "type", length = 30)
    private String type;

    @Size(max = 20)
    @Column(name = "status", length = 20)
    private String status;

    @NotNull
    @Column(name = "issued_at", nullable = false)
    private Instant issuedAt;

    @NotNull
    @Column(name = "used_at", nullable = false)
    private Instant usedAt;

    @NotNull
    @Column(name = "customer_id", nullable = false)
    private Integer customerId;

    @NotNull
    @Column(name = "store_id", nullable = false)
    private Integer storeId;

}