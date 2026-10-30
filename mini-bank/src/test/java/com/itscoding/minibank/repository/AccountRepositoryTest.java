package com.itscoding.minibank.repository;

import com.itscoding.minibank.TestcontainersConfiguration;
import com.itscoding.minibank.domain.Account;
import com.itscoding.minibank.domain.Customer;
import org.hibernate.Hibernate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(TestcontainersConfiguration.class)   // 내장 DB(H2)가 없으므로 Testcontainers MySQL을 쓴다
class AccountRepositoryTest {

    @Autowired AccountRepository accountRepository;
    @Autowired CustomerRepository customerRepository;
    @Autowired TestEntityManager em;

    @Test
    void findAllWithCustomer는_고객까지_한_번에_가져온다() {
        Customer c = customerRepository.save(new Customer("홍길동", "010-1111-2222"));
        Account a = new Account("110-0000-0001", new BigDecimal("1000"));
        a.assignCustomer(c);                          // N:1 단방향 — 계좌 쪽에서 고객을 연결한다
        accountRepository.save(a);
        em.flush();
        em.clear();                                   // 1차 캐시를 비워 진짜로 DB에서 읽게 한다

        List<Account> accounts = accountRepository.findAllWithCustomer();

        assertThat(accounts).hasSize(1);
        // 프록시가 아니라 이미 로딩된 실제 객체인지 확인
        assertThat(Hibernate.isInitialized(accounts.get(0).getCustomer())).isTrue();
    }
}
