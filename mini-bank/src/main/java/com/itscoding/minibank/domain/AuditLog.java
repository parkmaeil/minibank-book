package com.itscoding.minibank.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

// 7.6절 REQUIRES_NEW 예제용 감사 기록
@Entity
@Table(name = "audit_log")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AuditLog {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String action;

    @Column(length = 500)
    private String detail;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    public AuditLog(String action, String detail, LocalDateTime createdAt) {
        this.action = action;
        this.detail = detail;
        this.createdAt = createdAt;
    }
}
