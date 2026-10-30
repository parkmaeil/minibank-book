package com.itscoding.minibank.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor     // final 필드 두 개(clock, properties)를 받는 생성자를 만들어 준다
public class StartupLogger implements CommandLineRunner {

    private final Clock clock;                     // 3.6절 — AppConfig의 @Bean
    private final MiniBankProperties properties;   // 3.7절 — 설정 파일 값

    @Override
    public void run(String... args) {
        log.info("미니뱅크가 시작되었습니다 — 현재 시각: {}", LocalDateTime.now(clock).withNano(0));
        log.info("계좌번호 앞자리={}, 기본 1일 이체 한도={}",
                properties.numberPrefix(), properties.defaultDailyLimit());
        log.debug("DEBUG 로그 — dev 프로파일에서만 보입니다");
    }
}
