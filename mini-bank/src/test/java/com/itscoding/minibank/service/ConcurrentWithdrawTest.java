package com.itscoding.minibank.service;

import com.itscoding.minibank.domain.Account;
import com.itscoding.minibank.dto.TransferRequestDto;
import com.itscoding.minibank.repository.AccountRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import java.math.BigDecimal;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import static com.itscoding.minibank.support.TestDtos.*;
import static org.assertj.core.api.Assertions.assertThat;

// 5.7절 동시 출금 실험과 7.8절 재시도 비교 — transfer()가 생긴 7장부터 실행할 수 있다
@SpringBootTest
class ConcurrentWithdrawTest {

    @Autowired AccountService accountService;
    @Autowired CustomerService customerService;
    @Autowired TransferFacade transferFacade;
    @Autowired AccountRepository accountRepository;

    @Test
    void 재시도_없이_동시에_10번_이체한다() throws Exception {
        runConcurrently("재시도 없음", request -> accountService.transfer(request));
    }

    @Test
    void 재시도하며_동시에_10번_이체한다() throws Exception {
        runConcurrently("재시도 있음", request -> transferFacade.transferWithRetry(request));
    }

    // 잔액 100,000원 계좌에서 10개 스레드가 같은 순간에 1,000원씩 이체한다
    private void runConcurrently(String label, Consumer<TransferRequestDto> transferCall) throws Exception {
        Long customerId = customerService.createCustomer(newCustomer("동시성"));
        String from = accountService.createAccount(newAccount(customerId, "100000")).getAccountNumber();
        String to = accountService.createAccount(newAccount(customerId, "0")).getAccountNumber();

        int threads = 10;
        ExecutorService pool = Executors.newFixedThreadPool(threads);
        CountDownLatch start = new CountDownLatch(1);
        AtomicInteger success = new AtomicInteger();
        AtomicInteger conflict = new AtomicInteger();

        for (int i = 0; i < threads; i++) {
            pool.submit(() -> {
                start.await();                         // 모든 스레드가 동시에 출발하도록 대기
                try {
                    transferCall.accept(transfer(from, to, "1000"));
                    success.incrementAndGet();
                } catch (ObjectOptimisticLockingFailureException e) {
                    conflict.incrementAndGet();
                }
                return null;
            });
        }
        start.countDown();
        pool.shutdown();
        pool.awaitTermination(30, TimeUnit.SECONDS);

        // 충돌이 몇 건이든, 성공한 횟수만큼만 정확히 출금되어야 한다
        Account a = accountRepository.findByAccountNumber(from).orElseThrow();
        assertThat(a.getBalance()).isEqualByComparingTo(
                new BigDecimal(100000 - 1000 * success.get()));
        System.out.printf("[%s] 성공 %d건, 충돌 %d건%n", label, success.get(), conflict.get());
    }
}
