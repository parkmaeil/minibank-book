package com.itscoding.minibank.exception;

// 19.4절 다른 고객의 계좌에 접근하려 할 때 (IDOR 방어)
public class AccountAccessDeniedException extends RuntimeException {
    public AccountAccessDeniedException(String accountNumber) {
        super("본인 계좌만 조회·이체할 수 있습니다. 계좌번호=" + accountNumber);
    }
}
