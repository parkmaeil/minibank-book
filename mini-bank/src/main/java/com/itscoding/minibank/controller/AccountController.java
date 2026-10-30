package com.itscoding.minibank.controller;

import com.itscoding.minibank.dto.AccountCreateRequestDto;
import com.itscoding.minibank.dto.AccountResponseDto;
import com.itscoding.minibank.service.AccountService;
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
}
