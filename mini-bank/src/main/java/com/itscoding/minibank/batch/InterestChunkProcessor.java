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

// 15.5절 계좌 묶음(청크) 하나를 한 트랜잭션으로 처리한다
// 별도 Bean으로 분리한 이유: 같은 클래스 안에서 부르면 @Transactional이 적용되지 않는다(7.7절)
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
                    .divide(BigDecimal.valueOf(365), 0, RoundingMode.DOWN);   // 원 단위 미만 절사
            if (interest.signum() > 0) {
                account.deposit(interest);
                historyRepository.save(new TransactionHistory(account, TransactionType.DEPOSIT,
                        interest, account.getBalance(), "이자 " + payDate));
            }
            interestLogRepository.save(new InterestLog(account.getId(), payDate, interest));
            paid++;
        }
        return paid;                                // 이번 실행에서 새로 처리한 계좌 수
    }
}
