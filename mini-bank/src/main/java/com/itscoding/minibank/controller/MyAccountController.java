package com.itscoding.minibank.controller;

import com.itscoding.minibank.dto.AccountResponseDto;
import com.itscoding.minibank.dto.TransactionResponseDto;
import com.itscoding.minibank.dto.TransferRequestDto;
import com.itscoding.minibank.service.AccountService;
import com.itscoding.minibank.service.TransferFacade;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// 17.4절 고객용 API — 토큰 속 customerId로 "내 것"만 다룬다
@Tag(name = "내 계좌(고객)", description = "로그인한 고객 자신의 계좌·거래내역·이체 API")
@SecurityRequirement(name = "bearerAuth")          // 17.5절 Swagger가 Basic 대신 Bearer 토큰을 보낸다
@RestController
@RequestMapping("/api/me")
@RequiredArgsConstructor
public class MyAccountController {

    private final AccountService accountService;
    private final TransferFacade transferFacade;     // 이체는 직원 API·관리 화면과 같은 입구로

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
            @ParameterObject @PageableDefault(size = 10) Pageable pageable) {
        return accountService.getMyTransactions(customerId(jwt), accountNumber, pageable);
    }

    @PostMapping("/transfers")
    public ResponseEntity<Void> transfer(@AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody TransferRequestDto request) {
        transferFacade.transferAsCustomer(customerId(jwt), request);
        return ResponseEntity.ok().build();
    }
}
