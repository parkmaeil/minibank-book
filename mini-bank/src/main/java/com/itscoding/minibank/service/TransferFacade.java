package com.itscoding.minibank.service;

import com.itscoding.minibank.dto.TransferRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import java.util.concurrent.ThreadLocalRandom;

// 7.8절 낙관적 락 충돌 시 재시도
@Service
@RequiredArgsConstructor
@Slf4j
public class TransferFacade {

    private static final int MAX_ATTEMPTS = 3;
    private final AccountService accountService;   // 다른 Bean → 프록시를 거쳐 매번 새 트랜잭션
    private final AuditService auditService;       // 12.2절 실패 기록도 이곳에서 — API와 화면이 같은 규칙을 쓴다

    // 이체의 단 하나의 입구: 충돌하면 재시도하고(7.8절), 끝내 실패하면 기록을 남긴다(7.6절)
    public void transferWithRetry(TransferRequestDto request) {
        try {
            retry(request);
        } catch (RuntimeException e) {
            auditService.recordFailure("TRANSFER", e.getMessage());  // 이체가 롤백돼도 기록은 남는다
            throw e;                                                 // 실패 사실은 호출한 쪽에 그대로 알린다
        }
    }

    private void retry(TransferRequestDto request) {
        for (int attempt = 1; ; attempt++) {
            try {
                accountService.transfer(request);
                return;
            } catch (ObjectOptimisticLockingFailureException e) {
                if (attempt >= MAX_ATTEMPTS) throw e;          // 끝내 실패하면 409로
                log.warn("낙관적 락 충돌, 재시도 {}/{}", attempt, MAX_ATTEMPTS);
                // 간격을 조금씩 늘리고(50ms, 100ms) 0~49ms의 무작위 값(지터)을 더해
                // 충돌한 요청들이 다시 같은 순간에 몰리지 않게 흩어 놓는다
                sleep(50L * attempt + ThreadLocalRandom.current().nextLong(50));
            }
        }
    }

    private void sleep(long millis) {
        try { Thread.sleep(millis); } catch (InterruptedException ie) { Thread.currentThread().interrupt(); }
    }
}
