package com.itscoding.minibank.service;

import com.itscoding.minibank.domain.Account;
import com.itscoding.minibank.domain.Customer;
import com.itscoding.minibank.dto.AccountCreateRequestDto;
import com.itscoding.minibank.dto.AccountResponseDto;
import com.itscoding.minibank.exception.AccountNotFoundException;
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
    private final CustomerRepository customerRepository;

    @Transactional
    public AccountResponseDto createAccount(AccountCreateRequestDto request) {
        log.info("계좌 개설 요청 - ownerName={}", request.getOwnerName());

        // 5장 임시 구현: 예금주명으로 고객을 새로 만들어 계좌를 연결한다.
        // 6장에서 고객 관리 API를 만든 뒤에는 고객 ID로 기존 고객을 찾아 연결한다.
        Customer owner = customerRepository.save(new Customer(request.getOwnerName(), null));
        Account account = new Account(generateAccountNumber(), request.getInitialBalance());
        account.assignCustomer(owner);         // 계좌 쪽에서 고객을 연결한다 (N:1 단방향)

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
        return accountRepository.findAll().stream()
                .map(AccountResponseDto::from)
                .toList();
    }

    private String generateAccountNumber() {
        return "110-" + (1000 + new Random().nextInt(9000))
                + "-" + (1000 + new Random().nextInt(9000));
    }
}
