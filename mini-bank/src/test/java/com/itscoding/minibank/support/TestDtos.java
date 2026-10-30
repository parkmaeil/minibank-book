package com.itscoding.minibank.support;

import com.itscoding.minibank.dto.AccountCreateRequestDto;
import com.itscoding.minibank.dto.CustomerCreateRequestDto;
import com.itscoding.minibank.dto.TransferRequestDto;

import java.math.BigDecimal;

// 책에서 "도우미 메서드(생략)"으로 표시한 DTO 생성 메서드 모음
public final class TestDtos {

    private TestDtos() {
    }

    public static CustomerCreateRequestDto newCustomer(String name) {
        CustomerCreateRequestDto dto = new CustomerCreateRequestDto();
        dto.setName(name);
        dto.setPhone("010-0000-0000");
        return dto;
    }

    public static AccountCreateRequestDto newAccount(Long customerId, String initialBalance) {
        AccountCreateRequestDto dto = new AccountCreateRequestDto();
        dto.setCustomerId(customerId);
        dto.setInitialBalance(new BigDecimal(initialBalance));
        return dto;
    }

    public static TransferRequestDto transfer(String from, String to, String amount) {
        TransferRequestDto dto = new TransferRequestDto();
        dto.setFromAccountNumber(from);
        dto.setToAccountNumber(to);
        dto.setAmount(new BigDecimal(amount));
        return dto;
    }
}
