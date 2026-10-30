package com.itscoding.minibank.security;

import com.itscoding.minibank.TestcontainersConfiguration;
import com.itscoding.minibank.service.AccountService;
import com.itscoding.minibank.service.CustomerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;

import java.math.BigDecimal;

import static com.itscoding.minibank.support.TestDtos.newAccount;
import static com.itscoding.minibank.support.TestDtos.newCustomer;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

// 14.9절 @PreAuthorize는 Spring이 만든 프록시에서 동작하므로 진짜 Bean으로 확인한다 (10.6절의 Testcontainers)
@SpringBootTest
@Import(TestcontainersConfiguration.class)
class DailyLimitSecurityTest {

    @Autowired AccountService accountService;
    @Autowired CustomerService customerService;

    @Test
    @WithMockUser(roles = "TELLER")
    void 창구_직원은_한도를_바꿀_수_없다() {
        Long accountId = newAccountId();

        assertThatThrownBy(() -> accountService.updateDailyLimit(accountId, new BigDecimal("100000")))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    @WithMockUser(roles = {"TELLER", "ADMIN"})
    void 관리자는_한도를_바꿀_수_있다() {
        Long accountId = newAccountId();

        assertThat(accountService.updateDailyLimit(accountId, new BigDecimal("100000")).getDailyLimit())
                .isEqualByComparingTo("100000");
    }

    private Long newAccountId() {
        Long customerId = customerService.createCustomer(newCustomer("보안"));
        return accountService.createAccount(newAccount(customerId, "10000")).getId();
    }
}
