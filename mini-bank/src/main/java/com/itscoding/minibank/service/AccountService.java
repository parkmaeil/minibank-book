package com.itscoding.minibank.service;

import com.itscoding.minibank.domain.Account;
import com.itscoding.minibank.domain.Customer;
import com.itscoding.minibank.dto.AccountCreateRequestDto;
import com.itscoding.minibank.dto.AccountResponseDto;
import com.itscoding.minibank.exception.AccountNotFoundException;
import com.itscoding.minibank.exception.CustomerNotFoundException;
import com.itscoding.minibank.repository.AccountRepository;
import com.itscoding.minibank.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountService {

    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;   // 고객 조회를 위해 추가

    @Transactional
    public AccountResponseDto createAccount(AccountCreateRequestDto request) {
        log.info("계좌 개설 요청 - customerId={}", request.getCustomerId());
        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new CustomerNotFoundException(request.getCustomerId()));

        Account account = new Account(generateAccountNumber(), request.getInitialBalance());
        account.assignCustomer(customer);      // N:1 단방향 — 계좌 쪽에서 고객을 연결한다 (6.6절)

        Account saved = accountRepository.save(account);
        return AccountResponseDto.from(saved);
    }

    @Transactional(readOnly = true)
    public AccountResponseDto getAccount(Long id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException(id));
        return AccountResponseDto.from(account);
    }

    @Transactional(readOnly = true)
    public List<AccountResponseDto> getAllAccounts() {
        return accountRepository.findAll().stream()   // 8장에서 N+1 문제를 해결한다
                .map(AccountResponseDto::from)
                .toList();
    }

    private String generateAccountNumber() {
        return "110-" + (1000 + new Random().nextInt(9000))
                + "-" + (1000 + new Random().nextInt(9000));
    }
}
