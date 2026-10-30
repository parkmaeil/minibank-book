package com.itscoding.minibank.service;

import com.itscoding.minibank.domain.Account;
import com.itscoding.minibank.domain.Customer;
import com.itscoding.minibank.domain.TransactionHistory;
import com.itscoding.minibank.domain.TransactionType;
import com.itscoding.minibank.dto.TransferRequestDto;
import com.itscoding.minibank.exception.AccountAccessDeniedException;
import com.itscoding.minibank.exception.DailyLimitExceededException;
import com.itscoding.minibank.exception.InsufficientBalanceException;
import com.itscoding.minibank.repository.AccountRepository;
import com.itscoding.minibank.repository.CustomerRepository;
import com.itscoding.minibank.repository.TransactionHistoryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {
    @Mock private AccountRepository accountRepository;
    @Mock private CustomerRepository customerRepository;
    @Mock private TransactionHistoryRepository transactionHistoryRepository;   // 12장 추가 — 없으면 transfer()에서 NPE
    @Mock private ApplicationEventPublisher eventPublisher;                    // 15장 추가
    @InjectMocks private AccountService accountService;

    @Test
    @DisplayName("계좌 이체 성공: 두 계좌의 잔액이 정확히 출금/입금된다")
    void 이체_성공시_두_계좌_잔액이_정확히_변경된다() {
        Account from = new Account("110-1111-1111", new BigDecimal("50000"));
        Account to = new Account("110-2222-2222", new BigDecimal("0"));
        given(accountRepository.findByAccountNumber("110-1111-1111")).willReturn(Optional.of(from));
        given(accountRepository.findByAccountNumber("110-2222-2222")).willReturn(Optional.of(to));

        TransferRequestDto request = new TransferRequestDto();
        request.setFromAccountNumber("110-1111-1111");
        request.setToAccountNumber("110-2222-2222");
        request.setAmount(new BigDecimal("20000"));

        accountService.transfer(request);

        assertThat(from.getBalance()).isEqualByComparingTo("30000");
        assertThat(to.getBalance()).isEqualByComparingTo("20000");
    }

    @Test
    @DisplayName("계좌 이체 실패: 잔액보다 큰 금액을 이체하면 예외가 터져야 한다")
    void 잔액이_부족하면_InsufficientBalanceException이_발생한다() {
        // given (출금 계좌에 1,000원밖에 없는 상황)
        Account from = new Account("110-1111-1111", new BigDecimal("1000"));
        Account to = new Account("110-2222-2222", new BigDecimal("0"));

        given(accountRepository.findByAccountNumber("110-1111-1111")).willReturn(Optional.of(from));
        given(accountRepository.findByAccountNumber("110-2222-2222")).willReturn(Optional.of(to));

        TransferRequestDto request = new TransferRequestDto();
        request.setFromAccountNumber("110-1111-1111");
        request.setToAccountNumber("110-2222-2222");
        request.setAmount(new BigDecimal("999999"));   // 100만 원 이체 시도!

        // when & then (예외가 정상적으로 터지는 것을 검증)
        assertThatThrownBy(() -> accountService.transfer(request))
                .isInstanceOf(InsufficientBalanceException.class)
                .hasMessageContaining("잔액이 부족합니다");
    }

    // 13.4절 추가 테스트
    @Test
    void 오늘_출금합계와_이번_금액이_한도를_넘으면_예외가_발생한다() {
        Account from = new Account("110-1111-1111", new BigDecimal("500000"));
        Account to = new Account("110-2222-2222", BigDecimal.ZERO);
        from.changeDailyLimit(new BigDecimal("100000"));
        given(accountRepository.findByAccountNumber("110-1111-1111")).willReturn(Optional.of(from));
        given(accountRepository.findByAccountNumber("110-2222-2222")).willReturn(Optional.of(to));
        // 오늘 이미 60,000원을 출금했다고 가정
        given(transactionHistoryRepository.findByAccountIdAndTypeAndTransactedAtBetween(
                any(), eq(TransactionType.WITHDRAW), any(), any()))
                .willReturn(List.of(new TransactionHistory(from, TransactionType.WITHDRAW,
                        new BigDecimal("60000"), new BigDecimal("440000"), "이전 이체")));

        TransferRequestDto request = new TransferRequestDto();
        request.setFromAccountNumber("110-1111-1111");
        request.setToAccountNumber("110-2222-2222");
        request.setAmount(new BigDecimal("50000"));   // 60,000 + 50,000 > 100,000

        assertThatThrownBy(() -> accountService.transfer(request))
                .isInstanceOf(DailyLimitExceededException.class);
        assertThat(from.getBalance()).isEqualByComparingTo("500000");   // 출금되지 않음
    }

    // 19.4절 소유권 검사 — 남의 계좌에서는 이체할 수 없다(IDOR 방어)
    @Test
    void 다른_고객의_계좌에서_이체하면_AccountAccessDeniedException이_발생한다() {
        Customer owner = new Customer("홍길동", "010-1111-2222");
        ReflectionTestUtils.setField(owner, "id", 1L);          // 저장하지 않았으므로 id를 직접 넣는다
        Account from = new Account("110-1111-1111", new BigDecimal("50000"));
        from.assignCustomer(owner);
        given(accountRepository.findByAccountNumber("110-1111-1111")).willReturn(Optional.of(from));

        TransferRequestDto request = new TransferRequestDto();
        request.setFromAccountNumber("110-1111-1111");
        request.setToAccountNumber("110-2222-2222");
        request.setAmount(new BigDecimal("10000"));

        assertThatThrownBy(() -> accountService.transferAsCustomer(2L, request))   // 2번 고객이 시도
                .isInstanceOf(AccountAccessDeniedException.class);
        assertThat(from.getBalance()).isEqualByComparingTo("50000");
    }
}
