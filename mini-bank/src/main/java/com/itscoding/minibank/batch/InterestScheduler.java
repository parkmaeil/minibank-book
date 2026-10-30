package com.itscoding.minibank.batch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalDate;

@Slf4j
@Component
@RequiredArgsConstructor
public class InterestScheduler {

    private final InterestService interestService;
    private final Clock clock;                              // 3.6절에서 등록한 Clock

    // 초 분 시 일 월 요일 — 매일 0시 0분 0초 (한국 시간)
    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")
    public void payDailyInterest() {
        LocalDate payDate = LocalDate.now(clock).minusDays(1);   // 어제분 이자
        int paid = interestService.payAll(payDate);
        log.info("이자 지급 완료 - 기준일={}, 계좌={}건", payDate, paid);
    }
}
