package com.itscoding.minibank.controller;

import com.itscoding.minibank.dto.CustomerCreateRequestDto;
import com.itscoding.minibank.dto.CustomerResponseDto;
import com.itscoding.minibank.dto.CustomerUpdateRequestDto;
import com.itscoding.minibank.service.CustomerService;
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

    @PostMapping
    public ResponseEntity<Map<String, Long>> create(
            @Valid @RequestBody CustomerCreateRequestDto request) {
        Long id = customerService.createCustomer(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("customerId", id));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponseDto> getCustomer(@PathVariable Long id) {
        return ResponseEntity.ok(customerService.getCustomer(id));
    }

    @GetMapping
    public ResponseEntity<List<CustomerResponseDto>> getAllCustomers() {
        return ResponseEntity.ok(customerService.getAllCustomers());
    }

    // 6.8절 — 예: GET /api/customers/search?keyword=길동
    @GetMapping("/search")
    public ResponseEntity<List<CustomerResponseDto>> search(@RequestParam String keyword) {
        return ResponseEntity.ok(customerService.searchCustomers(keyword));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateCustomer(
            @PathVariable Long id, @Valid @RequestBody CustomerUpdateRequestDto request) {
        customerService.updateCustomer(id, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {
        customerService.deleteCustomer(id);
        return ResponseEntity.noContent().build();
    }
}
