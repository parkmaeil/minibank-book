package com.itscoding.minibank.exception;

import java.math.BigDecimal;

public class DailyLimitExceededException extends RuntimeException {
    public DailyLimitExceededException(String accountNumber, BigDecimal limit, BigDecimal usedToday) {
        super(String.format("1일 이체 한도를 초과했습니다. 계좌=%s, 한도=%,d원, 오늘 사용액=%,d원",
                accountNumber, limit.longValue(), usedToday.longValue()));
    }
}
