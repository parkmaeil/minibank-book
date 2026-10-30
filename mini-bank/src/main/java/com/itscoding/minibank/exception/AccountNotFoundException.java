package com.itscoding.minibank.exception;

// 런타임 예외를 상속받아 커스텀 예외를 만듭니다.
public class AccountNotFoundException extends RuntimeException {
    public AccountNotFoundException(Long id) {
        super("계좌를 찾을 수 없습니다. id=" + id);
    }
}
