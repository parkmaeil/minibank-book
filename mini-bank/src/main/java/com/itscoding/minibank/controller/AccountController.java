package com.itscoding.minibank.controller;

import com.itscoding.minibank.dto.AccountCreateRequestDto;
import com.itscoding.minibank.dto.AccountResponseDto;
import com.itscoding.minibank.dto.TransferRequestDto;
import com.itscoding.minibank.service.AccountService;
import com.itscoding.minibank.service.AuditService;
import com.itscoding.minibank.service.TransferFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "계좌", description = "계좌 개설·조회·이체 API")
@RestController // JSON 데이터를 반환하는 RESTful 컨트롤러임을 명시합니다.
@RequestMapping("/api/accounts") // 이 컨트롤러의 기본 URL 경로를 설정합니다.
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;
    private final AuditService auditService;        // 7.6절 실패 기록(REQUIRES_NEW)
    private final TransferFacade transferFacade;    // 7.8절 낙관적 락 충돌 재시도

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

    // 🔥 [7장] 계좌 이체 엔드포인트
    @Operation(summary = "계좌 이체", description = "출금 계좌에서 입금 계좌로 금액을 옮깁니다. 동시 처리 충돌은 최대 3번까지 자동으로 다시 시도합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "이체 성공"),
            @ApiResponse(responseCode = "400", description = "입력값 검증 실패 또는 같은 계좌로 이체"),
            @ApiResponse(responseCode = "404", description = "계좌 없음"),
            @ApiResponse(responseCode = "409", description = "잔액 부족 또는 동시 처리 충돌")
    })
    @PostMapping("/transfer")
    public ResponseEntity<Void> transfer(@Valid @RequestBody TransferRequestDto request) {
        try {
            transferFacade.transferWithRetry(request);              // 7.8절 충돌 시 재시도
        } catch (RuntimeException e) {
            auditService.recordFailure("TRANSFER", e.getMessage());  // 7.6절 이체가 롤백돼도 기록은 남는다
            throw e;                                                 // 응답은 전역 예외 처리기가 만든다
        }
        return ResponseEntity.ok().build();   // 성공 시 200 OK
    }
}
