package com.itscoding.minibank.event;

import com.itscoding.minibank.TestcontainersConfiguration;
import com.itscoding.minibank.exception.InsufficientBalanceException;
import com.itscoding.minibank.service.AccountService;
import com.itscoding.minibank.service.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.test.context.event.RecordApplicationEvents;

import static com.itscoding.minibank.support.TestDtos.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

// 15.7절 이체가 성공했을 때만, 요청자 정보를 담은 이벤트가 한 번 발행되는지
@SpringBootTest
@Import(TestcontainersConfiguration.class)
@RecordApplicationEvents                 // 테스트 중 발행된 이벤트를 기록한다
class TransferEventTest {

    @Autowired AccountService accountService;
    @Autowired CustomerService customerService;
    @Autowired ApplicationEvents events;

    String a;
    String b;

    @BeforeEach
    void setUp() {
        // given: 계좌 A(50,000원), B(0원) 준비
        Long cid = customerService.createCustomer(newCustomer("이벤트"));
        a = accountService.createAccount(newAccount(cid, "50000")).getAccountNumber();
        b = accountService.createAccount(newAccount(cid, "0")).getAccountNumber();
        events.clear();                    // 준비 과정에서 생긴 이벤트는 지운다
    }

    @Test
    @WithMockUser(username = "teller", roles = "TELLER")   // 14장 — teller가 요청한 이체
    void 이체가_성공하면_요청자를_담은_완료_이벤트가_한_번_발행된다() {
        accountService.transfer(transfer(a, b, "20000"));

        assertThat(events.stream(TransferCompletedEvent.class))
                .singleElement()
                .satisfies(e -> {
                    assertThat(e.fromAccountNumber()).isEqualTo(a);
                    assertThat(e.amount()).isEqualByComparingTo("20000");
                    assertThat(e.requestedBy()).isEqualTo("teller");   // 15.4절 비동기 리스너가 쓸 요청자
                });
    }

    @Test
    void 잔액이_부족하면_이벤트가_발행되지_않는다() {
        assertThatThrownBy(() -> accountService.transfer(transfer(a, b, "999999")))
                .isInstanceOf(InsufficientBalanceException.class);
        assertThat(events.stream(TransferCompletedEvent.class)).isEmpty();
    }
}
