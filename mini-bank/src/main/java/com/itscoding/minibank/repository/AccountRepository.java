package com.itscoding.minibank.repository;

import com.itscoding.minibank.domain.Account;
import com.itscoding.minibank.dto.AccountSummaryDto;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {

    Optional<Account> findByAccountNumber(String accountNumber);

    // 6.2절 고객의 계좌 수와 보유 여부 — Customer가 계좌 목록을 모르므로 계좌 쪽에 묻는다
    long countByCustomerId(Long customerId);

    boolean existsByCustomerId(Long customerId);

    // 19.4절 고객 한 명의 계좌 목록
    List<Account> findByCustomerIdOrderByIdAsc(Long customerId);

    // 5.7절 비관적 락 — SELECT ... FOR UPDATE
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT a FROM Account a WHERE a.accountNumber = :accountNumber")
    Optional<Account> findByAccountNumberForUpdate(@Param("accountNumber") String accountNumber);

    // 8.3절 계좌 + 예금주(Customer)를 SQL 한 번으로 조회 (N+1 해결)
    @Query("SELECT a FROM Account a JOIN FETCH a.customer")
    List<Account> findAllWithCustomer();

    // 8.5절 @EntityGraph — findAll()을 재정의하면서 customer를 함께 조회하도록 지정
    @Override
    @EntityGraph(attributePaths = "customer")
    List<Account> findAll();

    // 쿼리 메서드에도 그대로 붙일 수 있다
    @EntityGraph(attributePaths = "customer")
    List<Account> findByBalanceGreaterThan(BigDecimal amount);

    // 8.6절 DTO 프로젝션
    @Query("""
            SELECT new com.itscoding.minibank.dto.AccountSummaryDto(
                a.accountNumber, c.name, a.balance)
            FROM Account a JOIN a.customer c
            ORDER BY a.accountNumber
            """)
    List<AccountSummaryDto> findAllSummaries();

    // 15.5절 이자 지급 배치 — id만 페이지 단위로 조회
    @Query("SELECT a.id FROM Account a")
    List<Long> findIds(Pageable pageable);

    // 8.7절 페이징
    @Override
    @EntityGraph(attributePaths = "customer")
    Page<Account> findAll(Pageable pageable);
}
