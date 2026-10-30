package com.itscoding.minibank.repository;

import com.itscoding.minibank.domain.InterestLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface InterestLogRepository extends JpaRepository<InterestLog, Long> {

    boolean existsByAccountIdAndPayDate(Long accountId, LocalDate payDate);
}
