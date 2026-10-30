package com.itscoding.minibank.controller;

import com.itscoding.minibank.batch.InterestService;
import com.itscoding.minibank.security.CurrentUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

// 15.5절 이자 수동 실행 — 서버 장애로 자정 작업이 빠진 날을 다시 처리할 때 쓴다(관리자 전용, 14.6절 URL 규칙)
@Tag(name = "이자", description = "이자 지급 배치 — 관리자 전용")
@RestController
@RequestMapping("/api/interest")
@RequiredArgsConstructor
@Slf4j
public class InterestController {

    private final InterestService interestService;

    @Operation(summary = "이자 수동 지급",
            description = "지정한 날짜분 이자를 지급합니다. 이미 지급한 계좌는 건너뛰므로 여러 번 실행해도 안전합니다(멱등). 관리자(ADMIN)만 실행할 수 있습니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "지급 완료(이번 실행에서 새로 지급한 계좌 수)"),
            @ApiResponse(responseCode = "400", description = "오늘 또는 미래 날짜"),
            @ApiResponse(responseCode = "403", description = "관리자가 아님")
    })
    @PostMapping("/pay")
    public ResponseEntity<Map<String, Object>> pay(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        int paid = interestService.payAll(date);
        log.info("이자 수동 지급 - by={}, 기준일={}, 계좌={}건", CurrentUser.username(), date, paid);
        return ResponseEntity.ok(Map.of("payDate", date.toString(), "paidAccounts", paid));
    }
}
