package com.itscoding.minibank.service;

import com.itscoding.minibank.domain.Account;
import com.itscoding.minibank.domain.Customer;
import com.itscoding.minibank.dto.AccountCreateRequestDto;
import com.itscoding.minibank.dto.AccountResponseDto;
import com.itscoding.minibank.dto.TransferRequestDto;
import com.itscoding.minibank.exception.AccountNotFoundException;
import com.itscoding.minibank.exception.CustomerNotFoundException;
import com.itscoding.minibank.repository.AccountRepository;
import com.itscoding.minibank.repository.CustomerRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
        // findAll() → findAllWithCustomer() : N+1 해결 (8장)
        return accountRepository.findAllWithCustomer().stream()
                .map(AccountResponseDto::from)
                .toList();
    }

    // 8.7절 페이징
    @Transactional(readOnly = true)
    public Page<AccountResponseDto> getAccountPage(Pageable pageable) {
        return accountRepository.findAll(pageable).map(AccountResponseDto::from);
    }

    // 7.2절 이체 — 이 메서드 전체가 하나의 트랜잭션으로 묶인다
    @Transactional
    public void transfer(TransferRequestDto request) {
        if (request.getFromAccountNumber().equals(request.getToAccountNumber())) {
            throw new IllegalArgumentException("같은 계좌로는 이체할 수 없습니다.");
        }
        Account from = accountRepository.findByAccountNumber(request.getFromAccountNumber())
                .orElseThrow(() -> new AccountNotFoundException(request.getFromAccountNumber()));
        Account to = accountRepository.findByAccountNumber(request.getToAccountNumber())
                .orElseThrow(() -> new AccountNotFoundException(request.getToAccountNumber()));

        from.withdraw(request.getAmount());   // 잔액 부족 시 InsufficientBalanceException
        to.deposit(request.getAmount());
        // save()를 호출하지 않아도 Dirty Checking으로 두 계좌 모두 UPDATE된다
    }

    private String generateAccountNumber() {
        return "110-" + (1000 + new Random().nextInt(9000))
                + "-" + (1000 + new Random().nextInt(9000));
    }
}
