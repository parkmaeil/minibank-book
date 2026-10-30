package com.itscoding.minibank.repository;

import com.itscoding.minibank.TestcontainersConfiguration;
import com.itscoding.minibank.domain.Account;
import com.itscoding.minibank.domain.Customer;
import com.itscoding.minibank.dto.CustomerResponseDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

// 10.5절 — 6.6절의 N:1 단방향 설계에서 "고객의 계좌 수"를 쿼리로 구하는 부분을 검증한다
@DataJpaTest
@Import(TestcontainersConfiguration.class)
class CustomerRepositoryTest {

    @Autowired CustomerRepository customerRepository;
    @Autowired AccountRepository accountRepository;
    @Autowired TestEntityManager em;

    @Test
    void 고객별_계좌_수를_한_번에_세고_계좌가_없는_고객도_포함한다() {
        Customer hong = customerRepository.save(new Customer("홍길동", "010-1111-2222"));
        Customer kim = customerRepository.save(new Customer("김길동", "010-3333-4444"));   // 계좌 없음
        openAccount(hong, "110-0000-0001");
        openAccount(hong, "110-0000-0002");
        em.flush();
        em.clear();

        List<CustomerResponseDto> result = customerRepository.findAllWithAccountCount();

        assertThat(result)
                .extracting(CustomerResponseDto::getName, CustomerResponseDto::getAccountCount)
                .containsExactly(tuple("홍길동", 2L), tuple("김길동", 0L));   // LEFT JOIN 덕분에 김길동도 남는다
        assertThat(accountRepository.countByCustomerId(hong.getId())).isEqualTo(2);
        assertThat(accountRepository.existsByCustomerId(kim.getId())).isFalse();
    }

    private void openAccount(Customer owner, String accountNumber) {
        Account account = new Account(accountNumber, new BigDecimal("1000"));
        account.assignCustomer(owner);
        accountRepository.save(account);
    }
}
