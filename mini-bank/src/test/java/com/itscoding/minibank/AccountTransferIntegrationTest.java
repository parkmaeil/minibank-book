package com.itscoding.minibank;

import com.itscoding.minibank.exception.InsufficientBalanceException;
import com.itscoding.minibank.repository.AccountRepository;
import com.itscoding.minibank.service.AccountService;
import com.itscoding.minibank.service.CustomerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.mysql.MySQLContainer;

import java.math.BigDecimal;

import static com.itscoding.minibank.support.TestDtos.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Testcontainers
class AccountTransferIntegrationTest {

    @Container
    @ServiceConnection            // 컨테이너의 접속 정보를 spring.datasource.*에 자동 연결
    static MySQLContainer mysql = new MySQLContainer("mysql:8.4.0");

    @Autowired AccountService accountService;
    @Autowired CustomerService customerService;
    @Autowired AccountRepository accountRepository;

    @Test
    void 실제_MySQL에서_이체가_원자적으로_처리된다() {
        Long cid = customerService.createCustomer(newCustomer("통합"));
        String a = accountService.createAccount(newAccount(cid, "50000")).getAccountNumber();
        String b = accountService.createAccount(newAccount(cid, "0")).getAccountNumber();

        assertThatThrownBy(() -> accountService.transfer(transfer(a, b, "999999")))
                .isInstanceOf(InsufficientBalanceException.class);

        assertThat(balanceOf(a)).isEqualByComparingTo("50000");   // 실패한 이체는 흔적이 없다
    }

    private BigDecimal balanceOf(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber).orElseThrow().getBalance();
    }
}
