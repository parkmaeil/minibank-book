package com.itscoding.minibank.controller;

import com.itscoding.minibank.dto.CustomerCreateRequestDto;
import com.itscoding.minibank.dto.CustomerResponseDto;
import com.itscoding.minibank.dto.CustomerUpdateRequestDto;
import com.itscoding.minibank.service.CustomerService;
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
import java.util.Map;

@Tag(name = "고객", description = "고객 등록·조회·수정·삭제 API")
@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @Operation(summary = "고객 등록", description = "새 고객을 등록하고 발급된 고객 ID를 돌려줍니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "등록 성공"),
            @ApiResponse(responseCode = "400", description = "입력값 검증 실패")
    })
    @PostMapping
    public ResponseEntity<Map<String, Long>> create(
            @Valid @RequestBody CustomerCreateRequestDto request) {
        Long id = customerService.createCustomer(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("customerId", id));
    }

    @Operation(summary = "고객 조회", description = "고객 정보와 보유 계좌 수를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "고객 없음")
    })
    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponseDto> getCustomer(@PathVariable Long id) {
        return ResponseEntity.ok(customerService.getCustomer(id));
    }

    @Operation(summary = "고객 목록", description = "전체 고객과 고객별 보유 계좌 수를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공")
    })
    @GetMapping
    public ResponseEntity<List<CustomerResponseDto>> getAllCustomers() {
        return ResponseEntity.ok(customerService.getAllCustomers());
    }

    // 6.8절 — 예: GET /api/customers/search?keyword=길동
    @Operation(summary = "고객 검색", description = "이름의 일부로 고객을 찾습니다. 결과는 이름순입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "검색 성공")
    })
    @GetMapping("/search")
    public ResponseEntity<List<CustomerResponseDto>> search(@RequestParam String keyword) {
        return ResponseEntity.ok(customerService.searchCustomers(keyword));
    }

    @Operation(summary = "고객 정보 수정", description = "이름과 연락처를 수정합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "수정 성공"),
            @ApiResponse(responseCode = "400", description = "입력값 검증 실패"),
            @ApiResponse(responseCode = "404", description = "고객 없음")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateCustomer(
            @PathVariable Long id, @Valid @RequestBody CustomerUpdateRequestDto request) {
        customerService.updateCustomer(id, request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "고객 삭제", description = "계좌가 없는 고객만 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "삭제 성공"),
            @ApiResponse(responseCode = "404", description = "고객 없음"),
            @ApiResponse(responseCode = "409", description = "보유 계좌가 있어 삭제할 수 없음")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {
        customerService.deleteCustomer(id);
        return ResponseEntity.noContent().build();
    }
}
