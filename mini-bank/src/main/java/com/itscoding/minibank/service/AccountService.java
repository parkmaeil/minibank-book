package com.itscoding.minibank.service;

import com.itscoding.minibank.domain.Account;
import com.itscoding.minibank.domain.Customer;
import com.itscoding.minibank.domain.TransactionHistory;
import com.itscoding.minibank.domain.TransactionType;
import com.itscoding.minibank.dto.AccountCreateRequestDto;
import com.itscoding.minibank.dto.AccountResponseDto;
import com.itscoding.minibank.dto.TransactionResponseDto;
import com.itscoding.minibank.dto.TransferRequestDto;
import com.itscoding.minibank.event.TransferCompletedEvent;
import com.itscoding.minibank.exception.AccountNotFoundException;
import com.itscoding.minibank.exception.CustomerNotFoundException;
import com.itscoding.minibank.exception.DailyLimitExceededException;
import com.itscoding.minibank.repository.AccountRepository;
import com.itscoding.minibank.repository.CustomerRepository;
import com.itscoding.minibank.repository.TransactionHistoryRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountService {

    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;   // 고객 조회를 위해 추가
    // 🔥 12장 필드 추가
    private final TransactionHistoryRepository transactionHistoryRepository;
    private final ApplicationEventPublisher eventPublisher;   // 15.2절 생성자 주입 필드에 추가

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

        validateDailyLimit(from, request.getAmount());   // 🔥 13장 출금 전에 한도 검증

        from.withdraw(request.getAmount());   // 잔액 부족 시 InsufficientBalanceException
        to.deposit(request.getAmount());
        // save()를 호출하지 않아도 Dirty Checking으로 두 계좌 모두 UPDATE된다

        // 12.4절 거래내역도 같은 트랜잭션 안에서 저장한다
        transactionHistoryRepository.save(new TransactionHistory(
                from, TransactionType.WITHDRAW, request.getAmount(),
                from.getBalance(), "이체 출금 → " + to.getAccountNumber()));
        transactionHistoryRepository.save(new TransactionHistory(
                to, TransactionType.DEPOSIT, request.getAmount(),
                to.getBalance(), "이체 입금 ← " + from.getAccountNumber()));

        // 15.2절 이체 완료 이벤트 발행 — 실제 처리는 커밋 뒤 리스너가 맡는다
        eventPublisher.publishEvent(new TransferCompletedEvent(
                from.getAccountNumber(), to.getAccountNumber(),
                request.getAmount(), LocalDateTime.now()));
    }

    // 🔥 12.4절 거래내역 조회
    @Transactional(readOnly = true)
    public List<TransactionResponseDto> getTransactionHistory(Long accountId) {
        return transactionHistoryRepository.findByAccountIdOrderByTransactedAtDesc(accountId)
                .stream()
                .map(TransactionResponseDto::from)
                .toList();
    }

    // 12.7절 거래내역 기간 검색과 페이징
    @Transactional(readOnly = true)
    public Page<TransactionResponseDto> searchTransactions(
            Long accountId, LocalDate from, LocalDate to, Pageable pageable) {
        if (from.isAfter(to)) {
            throw new IllegalArgumentException("시작일이 종료일보다 늦을 수 없습니다.");
        }
        return transactionHistoryRepository
                .findByAccountIdAndTransactedAtBetween(
                        accountId, from.atStartOfDay(), to.plusDays(1).atStartOfDay(), pageable)
                .map(TransactionResponseDto::from);
    }

    // 13장 1일 이체 한도 변경
    @Transactional
    public AccountResponseDto updateDailyLimit(Long accountId, BigDecimal newLimit) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException(accountId));
        account.changeDailyLimit(newLimit);   // Dirty Checking으로 UPDATE
        return AccountResponseDto.from(account);
    }

    private void validateDailyLimit(Account account, BigDecimal requestAmount) {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1);
        BigDecimal usedToday = transactionHistoryRepository
                .findByAccountIdAndTypeAndTransactedAtBetween(
                        account.getId(), TransactionType.WITHDRAW, startOfDay, endOfDay)
                .stream().map(TransactionHistory::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (usedToday.add(requestAmount).compareTo(account.getDailyLimit()) > 0) {
            throw new DailyLimitExceededException(
                    account.getAccountNumber(), account.getDailyLimit(), usedToday);
        }
    }

    private String generateAccountNumber() {
        return "110-" + (1000 + new Random().nextInt(9000))
                + "-" + (1000 + new Random().nextInt(9000));
    }
}
