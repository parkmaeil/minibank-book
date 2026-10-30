package com.itscoding.minibank.event;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// 이벤트는 “이미 일어난 사실”이므로 과거형 이름과 불변 객체(record)로 만든다
public record TransferCompletedEvent(
        String fromAccountNumber,
        String toAccountNumber,
        BigDecimal amount,
        LocalDateTime occurredAt) {
}
