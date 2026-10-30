package com.itscoding.minibank.batch;

import com.itscoding.minibank.domain.Account;
import com.itscoding.minibank.domain.InterestLog;
import com.itscoding.minibank.domain.TransactionHistory;
import com.itscoding.minibank.domain.TransactionType;
import com.itscoding.minibank.repository.AccountRepository;
import com.itscoding.minibank.repository.InterestLogRepository;
import com.itscoding.minibank.repository.TransactionHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class InterestChunkProcessor {

    private final AccountRepository accountRepository;
    private final InterestLogRepository interestLogRepository;
    private final TransactionHistoryRepository historyRepository;

    @Transactional
    public int process(List<Long> accountIds, LocalDate payDate, BigDecimal annualRate) {
        int paid = 0;
        for (Account account : accountRepository.findAllById(accountIds)) {
            if (interestLogRepository.existsByAccountIdAndPayDate(account.getId(), payDate)) {
                continue;                           // 이미 지급했다면 건너뛴다(멱등성)
            }
            BigDecimal interest = account.getBalance()
                    .multiply(annualRate)
                    // 원 단위 절사
                    .divide(BigDecimal.valueOf(365), 0, RoundingMode.DOWN);
            if (interest.signum() > 0) {
                account.deposit(interest);
                historyRepository.save(new TransactionHistory(account, TransactionType.DEPOSIT,
                        interest, account.getBalance(), "이자 " + payDate));
            }
            interestLogRepository.save(new InterestLog(account.getId(), payDate, interest));
            paid++;
        }
        return paid;
    }
}
