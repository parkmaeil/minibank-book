package com.itscoding.minibank.dto;

import com.itscoding.minibank.domain.Account;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class AccountResponseDto {

    private Long id;
    private String accountNumber;
    private String ownerName;
    private BigDecimal balance;
    private BigDecimal dailyLimit;          // 13장 추가
    private LocalDateTime openedAt;

    // 엔티티(Account)를 DTO로 안전하게 변환하는 정적 팩토리 메서드
    public static AccountResponseDto from(Account account) {
        AccountResponseDto dto = new AccountResponseDto();
        dto.id = account.getId();
        dto.accountNumber = account.getAccountNumber();
        // 연관관계로 연결된 Customer 객체를 따라가 이름을 꺼낸다
        dto.ownerName = account.getCustomer().getName();
        dto.balance = account.getBalance();
        dto.dailyLimit = account.getDailyLimit();
        dto.openedAt = account.getOpenedAt();
        return dto;
    }

    // --- Generate(⌘N / Alt+Insert)로 생성된 Getter / Setter ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }

    public String getOwnerName() { return ownerName; }
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }

    public BigDecimal getBalance() { return balance; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }

    public BigDecimal getDailyLimit() { return dailyLimit; }
    public void setDailyLimit(BigDecimal dailyLimit) { this.dailyLimit = dailyLimit; }

    public LocalDateTime getOpenedAt() { return openedAt; }
    public void setOpenedAt(LocalDateTime openedAt) { this.openedAt = openedAt; }
}
