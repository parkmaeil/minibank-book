package com.itscoding.minibank.controller;

import com.itscoding.minibank.dto.AccountCreateRequestDto;
import com.itscoding.minibank.dto.AccountResponseDto;
import com.itscoding.minibank.dto.DailyLimitRequestDto;
import com.itscoding.minibank.dto.TransactionResponseDto;
import com.itscoding.minibank.dto.TransferRequestDto;
import com.itscoding.minibank.service.AccountService;
import com.itscoding.minibank.service.TransferFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "계좌", description = "계좌 개설·조회·이체 API")
@RestController // JSON 데이터를 반환하는 RESTful 컨트롤러임을 명시합니다.
@RequestMapping("/api/accounts") // 이 컨트롤러의 기본 URL 경로를 설정합니다.
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;
    private final TransferFacade transferFacade;    // 7.8절 재시도 + 12.2절 실패 기록까지 맡는 이체 입구

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
    @Operation(summary = "계좌 목록 페이지 조회", description = "page(0부터)·size·sort(예: balance,desc)로 계좌를 나눠 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공")
    })
    @GetMapping("/page")
    public ResponseEntity<Page<AccountResponseDto>> getAccountPage(
            @ParameterObject @PageableDefault(size = 10, sort = "id") Pageable pageable) {
        return ResponseEntity.ok(accountService.getAccountPage(pageable));
    }

    // 🔥 [7장] 계좌 이체 엔드포인트
    @Operation(summary = "계좌 이체", description = "출금 계좌에서 입금 계좌로 금액을 옮깁니다. 동시 처리 충돌은 최대 3번까지 자동으로 다시 시도합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "이체 성공"),
            @ApiResponse(responseCode = "400", description = "입력값 검증 실패 또는 같은 계좌로 이체"),
            @ApiResponse(responseCode = "404", description = "계좌 없음"),
            @ApiResponse(responseCode = "409", description = "잔액 부족, 1일 이체 한도 초과 또는 동시 처리 충돌")
    })
    @PostMapping("/transfer")
    public ResponseEntity<Void> transfer(@Valid @RequestBody TransferRequestDto request) {
        transferFacade.transferWithRetry(request);   // 재시도·실패 기록은 Facade가 맡는다(12.2절), 실패 응답은 전역 예외 처리기가
        return ResponseEntity.ok().build();   // 성공 시 200 OK
    }

    // 12.4절 거래내역 조회 (최신순)
    @Operation(summary = "계좌 거래내역 조회", description = "계좌 id로 거래내역을 최신순으로 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공(거래가 없으면 빈 목록)")
    })
    @GetMapping("/{id}/transactions")
    public ResponseEntity<List<TransactionResponseDto>> getTransactions(@PathVariable Long id) {
        return ResponseEntity.ok(accountService.getTransactionHistory(id));
    }

    // 12.7절 — 예: GET /api/accounts/1/transactions/search?from=2026-10-01&to=2026-10-31&page=0&size=20
    @Operation(summary = "거래내역 기간 검색", description = "시작일~종료일(종료일 포함) 거래를 최신순으로 나눠 조회합니다. 날짜는 2026-11-01 형식입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "400", description = "시작일이 종료일보다 늦음 또는 날짜 형식 오류")
    })
    @GetMapping("/{id}/transactions/search")
    public ResponseEntity<Page<TransactionResponseDto>> searchTransactions(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @ParameterObject @PageableDefault(size = 20, sort = "transactedAt", direction = Sort.Direction.DESC)
            Pageable pageable) {
        return ResponseEntity.ok(accountService.searchTransactions(id, from, to, pageable));
    }

    // 13장 1일 이체 한도 변경 — 일부 속성만 바꾸므로 PATCH(4장)
    @Operation(summary = "1일 이체 한도 변경", description = "계좌의 1일 이체 한도를 바꿉니다. 0보다 큰 금액이어야 하며, 관리자(ADMIN)만 바꿀 수 있습니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "변경 성공(바뀐 한도가 담긴 계좌 정보)"),
            @ApiResponse(responseCode = "400", description = "한도 누락 또는 0 이하"),
            @ApiResponse(responseCode = "403", description = "관리자가 아님(14.8절)"),
            @ApiResponse(responseCode = "404", description = "계좌 없음")
    })
    @PatchMapping("/{id}/limit")
    public ResponseEntity<AccountResponseDto> updateDailyLimit(
            @PathVariable Long id, @Valid @RequestBody DailyLimitRequestDto request) {
        return ResponseEntity.ok(accountService.updateDailyLimit(id, request.getDailyLimit()));
    }

    // 12.8절 CSV 다운로드 — CSV는 Service가 만들고(화면과 공유), 여기서는 파일 이름만 붙인다
    @Operation(summary = "거래내역 CSV 다운로드", description = "거래내역을 UTF-8(BOM) CSV 파일로 내려받습니다.")
    @GetMapping(value = "/{id}/transactions.csv", produces = "text/csv")
    public ResponseEntity<byte[]> downloadTransactions(@PathVariable Long id) {
        // Content-Disposition: attachment → 브라우저가 화면에 띄우지 않고 파일로 저장한다
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"transactions-" + id + ".csv\"")
                .body(accountService.exportTransactionsCsv(id));
    }
}
