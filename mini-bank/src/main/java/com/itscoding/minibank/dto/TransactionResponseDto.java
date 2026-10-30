package com.itscoding.minibank.dto;

import com.itscoding.minibank.domain.TransactionHistory;
import com.itscoding.minibank.domain.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransactionResponseDto {
    private Long id;
    private TransactionType type;
    private BigDecimal amount;
    private BigDecimal balanceAfter;
    private LocalDateTime transactedAt;
    private String description;

    public static TransactionResponseDto from(TransactionHistory history) {
        TransactionResponseDto dto = new TransactionResponseDto();
        dto.id = history.getId();
        dto.type = history.getType();
        dto.amount = history.getAmount();
        dto.balanceAfter = history.getBalanceAfter();
        dto.transactedAt = history.getTransactedAt();
        dto.description = history.getDescription();
        return dto;
    }

    public Long getId() {
        return id;
    }

    public TransactionType getType() {
        return type;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public BigDecimal getBalanceAfter() {
        return balanceAfter;
    }

    public LocalDateTime getTransactedAt() {
        return transactedAt;
    }

    public String getDescription() {
        return description;
    }
}
