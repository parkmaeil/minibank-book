package com.itscoding.minibank.domain;

import com.itscoding.minibank.exception.InsufficientBalanceException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "account")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Account {

    public static final BigDecimal DEFAULT_DAILY_LIMIT = new BigDecimal("3000000");

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String accountNumber;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal balance;

    @Column(nullable = false)
    private LocalDateTime openedAt;

    // 여러 계좌는 한 고객에게 속한다 (N:1 단방향) — FK(customer_id)를 가진 쪽만 상대를 참조한다
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @Version                       // 계좌 이체 동시성 제어용 낙관적 락
    private Long version;

    // 13장 1일 이체 한도
    @Column(precision = 15, scale = 2)
    private BigDecimal dailyLimit;

    public Account(String accountNumber, BigDecimal balance) {
        this.accountNumber = accountNumber;
        this.balance = balance;
        this.openedAt = LocalDateTime.now();
        this.dailyLimit = DEFAULT_DAILY_LIMIT;   // 신규 계좌 기본 한도
    }

    // 계좌를 고객에게 연결한다 — 이 값이 그대로 account.customer_id(FK)로 저장된다
    public void assignCustomer(Customer customer) { this.customer = customer; }

    public void withdraw(BigDecimal amount) {
        if (this.balance.compareTo(amount) < 0) {
            throw new InsufficientBalanceException(this.accountNumber);
        }
        this.balance = this.balance.subtract(amount);
    }

    public void deposit(BigDecimal amount) { this.balance = this.balance.add(amount); }

    // 기존 계좌(컬럼 추가 전 데이터)는 null일 수 있으므로 기본값으로 보정한다
    public BigDecimal getDailyLimit() {
        return dailyLimit != null ? dailyLimit : DEFAULT_DAILY_LIMIT;
    }

    public void changeDailyLimit(BigDecimal newLimit) {
        if (newLimit == null || newLimit.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("한도는 0보다 커야 합니다.");
        }
        this.dailyLimit = newLimit;
    }
}
