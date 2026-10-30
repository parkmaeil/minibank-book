package com.itscoding.minibank.repository;

import com.itscoding.minibank.domain.InterestLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface InterestLogRepository extends JpaRepository<InterestLog, Long> {

    // 15.5절 이 계좌에 이 날짜 이자를 이미 지급했는가 (멱등성)
    boolean existsByAccountIdAndPayDate(Long accountId, LocalDate payDate);
}
