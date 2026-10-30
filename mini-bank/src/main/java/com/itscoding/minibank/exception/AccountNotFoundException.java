package com.itscoding.minibank.exception;

public class AccountNotFoundException extends RuntimeException {
    public AccountNotFoundException(Long id) {
        super("계좌를 찾을 수 없습니다. id=" + id);
    }

    // 🔥 [7장 추가] 계좌번호(String)로 조회할 때 사용
    public AccountNotFoundException(String accountNumber) {
        super("계좌를 찾을 수 없습니다. 계좌번호=" + accountNumber);
    }
}
