package com.itscoding.minibank.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.math.BigDecimal;

@ConfigurationProperties(prefix = "minibank.account")
public record MiniBankProperties(
        String numberPrefix,             // number-prefix 와 자동으로 연결된다
        BigDecimal defaultDailyLimit) {  // default-daily-limit 와 연결된다
}
