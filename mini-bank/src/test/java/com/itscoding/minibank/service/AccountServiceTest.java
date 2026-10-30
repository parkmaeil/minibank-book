package com.itscoding.minibank.service;

import com.itscoding.minibank.domain.Account;
import com.itscoding.minibank.dto.TransferRequestDto;
import com.itscoding.minibank.exception.InsufficientBalanceException;
import com.itscoding.minibank.repository.AccountRepository;
import com.itscoding.minibank.repository.CustomerRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {
    @Mock private AccountRepository accountRepository;
    @Mock private CustomerRepository customerRepository;
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
}
