package com.itscoding.minibank.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public class AccountCreateRequestDto {

    @Schema(description = "계좌를 개설할 고객 ID", example = "1")
    @NotNull(message = "고객 ID는 필수입니다.")
    private Long customerId;

    @Schema(description = "초기 입금액(원)", example = "50000")
    @NotNull(message = "초기 입금액은 필수입니다.")
    @PositiveOrZero(message = "초기 입금액은 0 이상이어야 합니다.")
    private BigDecimal initialBalance;

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }
    public BigDecimal getInitialBalance() { return initialBalance; }
    public void setInitialBalance(BigDecimal initialBalance) { this.initialBalance = initialBalance; }
}
