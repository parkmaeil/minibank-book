package com.itscoding.minibank.service;

import com.itscoding.minibank.dto.TransferRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

// 7.8절 낙관적 락 충돌 시 재시도
@Service
@RequiredArgsConstructor
@Slf4j
public class TransferFacade {

    private static final int MAX_ATTEMPTS = 3;
    private final AccountService accountService;   // 다른 Bean → 프록시를 거쳐 매번 새 트랜잭션

    public void transferWithRetry(TransferRequestDto request) {
        for (int attempt = 1; ; attempt++) {
            try {
                accountService.transfer(request);
                return;
            } catch (ObjectOptimisticLockingFailureException e) {
                if (attempt >= MAX_ATTEMPTS) throw e;          // 끝내 실패하면 409로
                log.warn("낙관적 락 충돌, 재시도 {}/{}", attempt, MAX_ATTEMPTS);
                sleep(50L * attempt);                           // 조금씩 간격을 늘려 다시 시도
            }
        }
    }

    private void sleep(long millis) {
        try { Thread.sleep(millis); } catch (InterruptedException ie) { Thread.currentThread().interrupt(); }
    }
}
