package com.itscoding.minibank.batch;

import com.itscoding.minibank.TestcontainersConfiguration;
import com.itscoding.minibank.repository.AccountRepository;
import com.itscoding.minibank.service.AccountService;
import com.itscoding.minibank.service.CustomerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;

import static com.itscoding.minibank.support.TestDtos.newAccount;
import static com.itscoding.minibank.support.TestDtos.newCustomer;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

// 15.7절 스케줄 작업은 cron 시각을 기다리지 않고, 스케줄러가 부르는 메서드를 직접 호출해 확인한다
@SpringBootTest
@Import(TestcontainersConfiguration.class)
class InterestServiceTest {

    @Autowired InterestService interestService;
    @Autowired AccountService accountService;
    @Autowired CustomerService customerService;
    @Autowired AccountRepository accountRepository;
    @Autowired Clock clock;

    @Test
    void 같은_날짜로_두_번_실행해도_이자는_한_번만_지급된다() {
        Long cid = customerService.createCustomer(newCustomer("이자"));
        String accountNumber = accountService.createAccount(newAccount(cid, "1000000")).getAccountNumber();

        LocalDate payDate = LocalDate.of(2026, 10, 1);
        interestService.payAll(payDate);
        BigDecimal afterFirst = balanceOf(accountNumber);
        // 1,000,000원 × 1.2% ÷ 365 = 32.87… → 32원(원 단위 절사)
        assertThat(afterFirst).isEqualByComparingTo("1000032");

        interestService.payAll(payDate);                    // 재실행

        assertThat(balanceOf(accountNumber)).isEqualByComparingTo(afterFirst);
    }

    @Test
    void 오늘_날짜의_이자는_지급할_수_없다() {
        LocalDate today = LocalDate.now(clock);

        assertThatThrownBy(() -> interestService.payAll(today))
                .isInstanceOf(IllegalArgumentException.class);
    }

    private BigDecimal balanceOf(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber).orElseThrow().getBalance();
    }
}
