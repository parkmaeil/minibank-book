package com.itscoding.minibank.batch;

import com.itscoding.minibank.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.util.List;

// 15.5절 이자 지급 — 스케줄러(매일 자정)와 관리자 수동 실행이 함께 쓰는 단 하나의 입구
@Service
@RequiredArgsConstructor
public class InterestService {

    private static final BigDecimal ANNUAL_RATE = new BigDecimal("0.012");   // 연 1.2%
    private static final int PAGE_SIZE = 500;                                 // 청크 크기

    private final AccountRepository accountRepository;
    private final InterestChunkProcessor chunkProcessor;
    private final Clock clock;                                                // 3.6절 "오늘"을 정하는 시계

    public int payAll(LocalDate payDate) {
        // 아직 끝나지 않은 날(오늘·미래)의 이자는 지급하지 않는다
        if (!payDate.isBefore(LocalDate.now(clock))) {
            throw new IllegalArgumentException("이자는 지난 날짜(어제까지)만 지급할 수 있습니다.");
        }
        int total = 0;
        Pageable page = PageRequest.of(0, PAGE_SIZE, Sort.by("id"));
        while (true) {
            List<Long> ids = accountRepository.findIds(page);      // id만 조회
            if (ids.isEmpty()) return total;
            total += chunkProcessor.process(ids, payDate, ANNUAL_RATE);   // 청크마다 별도 트랜잭션
            page = page.next();
        }
    }
}
