package com.itscoding.minibank.auth;

import com.itscoding.minibank.domain.Customer;
import com.itscoding.minibank.repository.CustomerRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// 17.3절 고객 로그인 — 아이디·비밀번호가 맞으면 JWT를 발급한다
@Tag(name = "고객 로그인", description = "고객이 로그인해 토큰(JWT)을 받는 API")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    @Operation(summary = "고객 로그인", description = "성공하면 accessToken과 고객 이름을 돌려줍니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그인 성공 — 토큰 발급"),
            @ApiResponse(responseCode = "400", description = "입력값 검증 실패"),
            @ApiResponse(responseCode = "401", description = "아이디 또는 비밀번호가 올바르지 않음")
    })
    @SecurityRequirements                 // 로그인은 인증 없이 부른다 — Swagger가 Basic 헤더를 붙이지 않는다
    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
        Customer customer = customerRepository.findByUsername(request.username())
                .filter(c -> passwordEncoder.matches(request.password(), c.getPassword()))
                .orElseThrow(() -> new BadCredentialsException("아이디 또는 비밀번호가 올바르지 않습니다."));
        return ResponseEntity.ok(new TokenResponse(tokenService.issue(customer), customer.getName()));
    }
}
