package com.itscoding.minibank.batch;

import com.itscoding.minibank.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InterestService {

    // 연 1.2%
    private static final BigDecimal ANNUAL_RATE = new BigDecimal("0.012");
    private static final int PAGE_SIZE = 500;

    private final AccountRepository accountRepository;
    private final InterestChunkProcessor chunkProcessor;

    public int payAll(LocalDate payDate) {
        int total = 0;
        Pageable page = PageRequest.of(0, PAGE_SIZE, Sort.by("id"));
        while (true) {
            List<Long> ids = accountRepository.findIds(page);      // id만 조회
            if (ids.isEmpty()) return total;
            // 청크마다 별도 트랜잭션
            total += chunkProcessor.process(ids, payDate, ANNUAL_RATE);
            page = page.next();
        }
    }
}
