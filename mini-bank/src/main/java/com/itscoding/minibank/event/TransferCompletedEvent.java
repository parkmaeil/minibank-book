package com.itscoding.minibank.event;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// 15.2절 이벤트는 "이미 일어난 사실"이므로 과거형 이름과 불변 객체(record)로 만든다
// 엔티티(Account) 대신 값만 담는다 — 리스너는 트랜잭션 밖·다른 스레드에서 실행될 수 있다
public record TransferCompletedEvent(
        String fromAccountNumber,
        String toAccountNumber,
        BigDecimal amount,
        String requestedBy,          // 15.4절 누가 요청했는가 — 비동기 스레드에는 로그인 정보가 따라가지 않으므로 값으로 담는다
        LocalDateTime occurredAt) {
}
