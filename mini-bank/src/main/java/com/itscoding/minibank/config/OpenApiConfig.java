package com.itscoding.minibank.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

// 14.6절 Swagger UI에 [Authorize] 버튼을 만들고, 모든 API 호출에 Basic 인증 헤더를 붙인다
@Configuration
@OpenAPIDefinition(security = @SecurityRequirement(name = "basicAuth"))
@SecurityScheme(name = "basicAuth", type = SecuritySchemeType.HTTP, scheme = "basic")
// 17.5절 고객 API(/api/me/**)는 Bearer 토큰(JWT)으로 시험한다 — [Authorize]에 칸이 하나 더 생긴다
@SecurityScheme(name = "bearerAuth", type = SecuritySchemeType.HTTP, scheme = "bearer", bearerFormat = "JWT")
public class OpenApiConfig {
}
