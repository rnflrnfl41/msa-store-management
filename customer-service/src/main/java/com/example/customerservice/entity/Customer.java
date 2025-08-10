package com.example.customerservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "customer")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "public_id", length = 36, unique = true)
    private String publicId;

    @NotBlank(message = "고객 이름은 필수입니다.")
    @Size(max = 50, message = "고객 이름은 50자를 초과할 수 없습니다.")
    @Column(name = "name", length = 50, nullable = false)
    private String name;

    @Size(max = 20, message = "전화번호는 20자를 초과할 수 없습니다.")
    @Column(name = "phone", length = 20)
    private String phone;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "store_id", nullable = false)
    private Integer storeId;

    // 기본 생성자에서 public_id 자동 생성
    @PrePersist
    public void generatePublicId() {
        if (publicId == null) {
            this.publicId = UUID.randomUUID().toString();
        }
    }
} 