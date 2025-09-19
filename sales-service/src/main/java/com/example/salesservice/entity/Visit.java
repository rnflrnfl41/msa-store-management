package com.example.salesservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "visit", schema = "sales_service")
public class Visit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @NotNull
    @Column(name = "visit_date", nullable = false)
    private LocalDate visitDate;

    @NotNull
    @Column(name = "visit_time", nullable = false)
    private LocalTime visitTime;

    @Lob
    @Column(name = "memo")
    private String memo;

    @Column(name = "created_at")
    @CreationTimestamp
    private LocalDateTime createdAt;

    @NotNull
    @Column(name = "store_id", nullable = false)
    private Integer storeId;

    @NotNull
    @Column(name = "customer_id", nullable = false)
    private Integer customerId;

    @NotNull
    @Column(name = "total_service_amount", nullable = false)
    private Integer totalServiceAmount;

    @NotNull
    @Column(name = "final_service_amount", nullable = false)
    private Integer finalServiceAmount;

    @OneToMany(mappedBy = "visit", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ServiceItem> serviceItems = new ArrayList<>();

}