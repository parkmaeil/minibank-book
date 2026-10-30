package com.itscoding.minibank.controller;

import com.itscoding.minibank.dto.AccountCreateRequestDto;
import com.itscoding.minibank.dto.AccountResponseDto;
import com.itscoding.minibank.dto.DailyLimitRequestDto;
import com.itscoding.minibank.dto.TransactionResponseDto;
import com.itscoding.minibank.dto.TransferRequestDto;
import com.itscoding.minibank.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

@Tag(name = "계좌", description = "계좌 개설·조회·이체 API")
@RestController // JSON 데이터를 반환하는 RESTful 컨트롤러임을 명시합니다.
@RequestMapping("/api/accounts") // 이 컨트롤러의 기본 URL 경로를 설정합니다.
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    // POST /api/accounts (계좌 생성)
    @Operation(summary = "계좌 개설", description = "등록된 고객에게 새 계좌를 개설합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "개설 성공"),
        @ApiResponse(responseCode = "400", description = "입력값 검증 실패"),
        @ApiResponse(responseCode = "404", description = "고객 없음")
    })
    @PostMapping
    public ResponseEntity<AccountResponseDto> createAccount(
            @Valid @RequestBody AccountCreateRequestDto request) {
        AccountResponseDto response = accountService.createAccount(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response); // 201 Created 반환
    }

    // GET /api/accounts/{id} (단건 조회)
    @Operation(summary = "계좌 단건 조회")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "404", description = "계좌 없음")
    })
    @GetMapping("/{id}")
    public ResponseEntity<AccountResponseDto> getAccount(@PathVariable Long id) {
        return ResponseEntity.ok(accountService.getAccount(id)); // 200 OK 반환
    }

    // GET /api/accounts (전체 조회)
    @Operation(summary = "계좌 전체 조회")
    @GetMapping
    public ResponseEntity<List<AccountResponseDto>> getAllAccounts() {
        return ResponseEntity.ok(accountService.getAllAccounts());
    }

    // 8.7절 — 예: GET /api/accounts/page?page=0&size=10&sort=balance,desc
    @Operation(summary = "계좌 목록 페이지 조회")
    @GetMapping("/page")
    public ResponseEntity<Page<AccountResponseDto>> getAccountPage(
            @PageableDefault(size = 10, sort = "id") Pageable pageable) {
        return ResponseEntity.ok(accountService.getAccountPage(pageable));
    }

    // 🔥 [7장] 계좌 이체 엔드포인트
    @Operation(summary = "계좌 이체")
    @PostMapping("/transfer")
    public ResponseEntity<Void> transfer(@Valid @RequestBody TransferRequestDto request) {
        accountService.transfer(request);
        return ResponseEntity.ok().build();   // 성공 시 200 OK
    }

    // 12.4절 거래내역 조회
    @Operation(summary = "계좌 거래내역 조회")
    @GetMapping("/{id}/transactions")
    public ResponseEntity<List<TransactionResponseDto>> getTransactions(@PathVariable Long id) {
        return ResponseEntity.ok(accountService.getTransactionHistory(id));
    }

    // 12.7절 — 예: GET /api/accounts/1/transactions/search?from=2026-10-01&to=2026-10-31&page=0&size=20
    @Operation(summary = "거래내역 기간 검색")
    @GetMapping("/{id}/transactions/search")
    public ResponseEntity<Page<TransactionResponseDto>> searchTransactions(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @PageableDefault(size = 20, sort = "transactedAt", direction = Sort.Direction.DESC)
            Pageable pageable) {
        return ResponseEntity.ok(accountService.searchTransactions(id, from, to, pageable));
    }

    // 13장 1일 이체 한도 변경
    @Operation(summary = "1일 이체 한도 변경")
    @PatchMapping("/{id}/limit")
    public ResponseEntity<AccountResponseDto> updateDailyLimit(
            @PathVariable Long id, @Valid @RequestBody DailyLimitRequestDto request) {
        return ResponseEntity.ok(accountService.updateDailyLimit(id, request.getDailyLimit()));
    }

    // 12.8절 CSV 다운로드
    @Operation(summary = "거래내역 CSV 다운로드")
    @GetMapping(value = "/{id}/transactions.csv", produces = "text/csv")
    public ResponseEntity<byte[]> downloadTransactions(@PathVariable Long id) {
        // UTF-8 BOM: 엑셀의 한글 깨짐 방지
        StringBuilder csv = new StringBuilder("﻿");
        csv.append("구분,금액,거래후잔액,거래일시,메모\n");
        for (TransactionResponseDto t : accountService.getTransactionHistory(id)) {
            csv.append(t.getType()).append(',')
               .append(t.getAmount()).append(',')
               .append(t.getBalanceAfter()).append(',')
               .append(t.getTransactedAt()).append(',')
               .append(escape(t.getDescription())).append('\n');
        }
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"transactions-" + id + ".csv\"")
                .body(csv.toString().getBytes(StandardCharsets.UTF_8));
    }

    // 값에 쉼표나 따옴표가 들어 있으면 큰따옴표로 감싸고, 안의 따옴표는 두 번 쓴다
    private String escape(String v) {
        if (v == null) return "";
        if (v.contains(",") || v.contains("\"") || v.contains("\n")) {
            return "\"" + v.replace("\"", "\"\"") + "\"";
        }
        return v;
    }
}
