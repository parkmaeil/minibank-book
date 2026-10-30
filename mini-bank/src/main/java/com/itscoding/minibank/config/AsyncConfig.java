package com.itscoding.minibank.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

// 15.4절 @Async를 켜고, 알림 전용 스레드 풀을 직접 정한다
@Configuration
@EnableAsync                           // @Async를 활성화한다
public class AsyncConfig {

    @Bean
    public ThreadPoolTaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);              // 평소에 유지하는 스레드 수
        executor.setMaxPoolSize(4);               // 대기열이 가득 차면 늘릴 수 있는 최대 수
        executor.setQueueCapacity(100);           // 처리 대기 작업 수
        executor.setThreadNamePrefix("notify-");  // 로그에서 알림 스레드를 알아보기 쉽게
        executor.initialize();
        return executor;
    }
}
