package com.itscoding.minibank.repository;

import com.itscoding.minibank.domain.TransactionHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface TransactionHistoryRepository extends JpaRepository<TransactionHistory, Long> {

    List<TransactionHistory> findByAccountIdOrderByTransactedAtDesc(Long accountId);

    // 12.7절 기간 검색과 페이징
    Page<TransactionHistory> findByAccountIdAndTransactedAtBetween(
            Long accountId, LocalDateTime start, LocalDateTime end, Pageable pageable);
}
