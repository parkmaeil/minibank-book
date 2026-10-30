package com.itscoding.minibank.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

// 15.5절 이자 지급 기록: (account_id, pay_date)에 UNIQUE 제약 — 같은 날 두 번 지급되지 않게 한다
@Entity
@Table(name = "interest_log",
       uniqueConstraints = @UniqueConstraint(columnNames = {"account_id", "pay_date"}))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class InterestLog {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "account_id", nullable = false)
    private Long accountId;

    @Column(name = "pay_date", nullable = false)
    private LocalDate payDate;

    @Column(precision = 15, scale = 2)
    private BigDecimal amount;

    public InterestLog(Long accountId, LocalDate payDate, BigDecimal amount) {
        this.accountId = accountId;
        this.payDate = payDate;
        this.amount = amount;
    }
}
