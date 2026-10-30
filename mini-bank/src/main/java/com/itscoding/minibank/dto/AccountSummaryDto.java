package com.itscoding.minibank.dto;

import java.math.BigDecimal;

public record AccountSummaryDto(String accountNumber, String ownerName, BigDecimal balance) {
}
