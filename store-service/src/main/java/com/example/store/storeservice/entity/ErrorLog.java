package com.example.store.storeservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Nationalized;

import java.time.Instant;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "error_log")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ErrorLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idx", nullable = false)
    private Integer id;

    @Size(max = 100)
    @Nationalized
    @Column(name = "error_id", length = 100)
    private String errorId;

    @Lob
    @Column(name = "message", columnDefinition = "TEXT")
    private String message;

    @Size(max = 50)
    @Nationalized
    @Column(name = "code", length = 50)
    private String code;

    @Column(name = "status")
    private Integer status;

    @Size(max = 100)
    @Nationalized
    @Column(name = "uri", length = 100)
    private String uri;

    @Size(max = 100)
    @Nationalized
    @Column(name = "method", length = 100)
    private String method;

    @Lob
    @Column(name = "stack_trace", columnDefinition = "TEXT")
    private String stackTrace;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Lob
    @Column(name = "request_params", columnDefinition = "TEXT")
    private String requestParams;
}
