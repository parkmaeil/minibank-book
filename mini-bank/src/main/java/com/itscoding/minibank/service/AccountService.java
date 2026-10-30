package com.itscoding.minibank.service;

import com.itscoding.minibank.domain.Account;
import com.itscoding.minibank.dto.AccountCreateRequestDto;
import com.itscoding.minibank.dto.AccountResponseDto;
import com.itscoding.minibank.exception.AccountNotFoundException;
import com.itscoding.minibank.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountService {

    private final AccountRepository accountRepository;

    public AccountResponseDto createAccount(AccountCreateRequestDto request) {
        log.info("계좌 개설 요청 - ownerName={}", request.getOwnerName());

        Account account = new Account();
        account.setAccountNumber(generateAccountNumber());
        account.setOwnerName(request.getOwnerName());
        account.setBalance(request.getInitialBalance());
        account.setOpenedAt(LocalDateTime.now());

        Account saved = accountRepository.save(account);
        log.debug("생성된 계좌번호: {}", saved.getAccountNumber());
        return AccountResponseDto.from(saved);
    }

    public AccountResponseDto getAccount(Long id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException(id));
        return AccountResponseDto.from(account);
    }

    public List<AccountResponseDto> getAllAccounts() {
        return accountRepository.findAll().stream()
                .map(AccountResponseDto::from)
                .toList();
    }

    private String generateAccountNumber() {
        // 실습 간소화를 위한 임시 채번 (110-XXXX-XXXX 형태)
        return "110-" + (1000 + new Random().nextInt(9000))
                + "-" + (1000 + new Random().nextInt(9000));
    }
}
