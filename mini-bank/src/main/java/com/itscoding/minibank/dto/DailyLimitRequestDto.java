package com.itscoding.minibank.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class DailyLimitRequestDto {
    @NotNull(message = "한도는 필수입니다.")
    @Positive(message = "한도는 0보다 커야 합니다.")
    private BigDecimal dailyLimit;
}
