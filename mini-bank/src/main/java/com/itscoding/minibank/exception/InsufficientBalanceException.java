package com.itscoding.minibank.exception;

public class InsufficientBalanceException extends RuntimeException {
    public InsufficientBalanceException(String accountNumber) {
        super("잔액이 부족합니다. accountNumber=" + accountNumber);
    }
}
