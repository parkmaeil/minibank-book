package com.itscoding.minibank.controller;

import com.itscoding.minibank.dto.AccountResponseDto;
import com.itscoding.minibank.dto.TransactionResponseDto;
import com.itscoding.minibank.dto.TransferRequestDto;
import com.itscoding.minibank.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// 19.4절 고객용 API — 토큰 속 customerId로 "내 것"만 다룬다
@RestController
@RequestMapping("/api/me")
@RequiredArgsConstructor
public class MyAccountController {

    private final AccountService accountService;

    private Long customerId(Jwt jwt) {
        return ((Number) jwt.getClaim("customerId")).longValue();   // 토큰 속 고객 id
    }

    @GetMapping("/accounts")
    public List<AccountResponseDto> myAccounts(@AuthenticationPrincipal Jwt jwt) {
        return accountService.getAccountsOf(customerId(jwt));
    }

    @GetMapping("/accounts/{accountNumber}/transactions")
    public Page<TransactionResponseDto> myTransactions(@AuthenticationPrincipal Jwt jwt,
            @PathVariable String accountNumber,
            @PageableDefault(size = 10) Pageable pageable) {
        return accountService.getMyTransactions(customerId(jwt), accountNumber, pageable);
    }

    @PostMapping("/transfers")
    public ResponseEntity<Void> transfer(@AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody TransferRequestDto request) {
        accountService.transferAsCustomer(customerId(jwt), request);
        return ResponseEntity.ok().build();
    }
}
