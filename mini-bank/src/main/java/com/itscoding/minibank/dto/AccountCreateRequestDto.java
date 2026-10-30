package com.itscoding.minibank.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public class AccountCreateRequestDto {

    @Schema(description = "예금주명", example = "홍길동")
    @NotBlank(message = "예금주명은 필수입니다.")
    private String ownerName;

    @Schema(description = "초기 입금액(원)", example = "50000")
    @NotNull
    @PositiveOrZero(message = "초기 입금액은 0 이상이어야 합니다.")
    private BigDecimal initialBalance;

    // --- Generate(⌘N / Alt+Insert)로 생성된 Getter / Setter ---
    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public BigDecimal getInitialBalance() {
        return initialBalance;
    }

    public void setInitialBalance(BigDecimal initialBalance) {
        this.initialBalance = initialBalance;
    }
}
