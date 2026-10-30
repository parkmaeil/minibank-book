package com.itscoding.minibank.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transaction_history")
@Getter
@NoArgsConstructor
public class TransactionHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;      // 관계의 주인 — Account 쪽엔 대응하는 컬렉션이 없다

    @Enumerated(EnumType.STRING)
    private TransactionType type; // WITHDRAW / DEPOSIT

    @Column(precision = 15, scale = 2)
    private BigDecimal amount;

    @Column(precision = 15, scale = 2)
    private BigDecimal balanceAfter;   // 거래 직후 잔액 스냅샷

    private LocalDateTime transactedAt;
    private String description;

    public TransactionHistory(Account account, TransactionType type,
                               BigDecimal amount, BigDecimal balanceAfter, String description) {
        this.account = account;
        this.type = type;
        this.amount = amount;
        this.balanceAfter = balanceAfter;
        this.transactedAt = LocalDateTime.now();
        this.description = description;
    }
}
