package com.itscoding.minibank.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class AccountCreateRequestDto {

    @Schema(description = "계좌를 개설할 고객 ID", example = "1")
    @NotNull(message = "고객 ID는 필수입니다.")
    private Long customerId;

    @Schema(description = "초기 입금액(원)", example = "50000")
    @NotNull(message = "초기 입금액은 필수입니다.")
    @PositiveOrZero(message = "초기 입금액은 0 이상이어야 합니다.")
    private BigDecimal initialBalance;
}
