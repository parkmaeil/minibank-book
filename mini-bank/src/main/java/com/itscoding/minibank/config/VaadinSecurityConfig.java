package com.itscoding.minibank.config;

import com.itscoding.minibank.ui.LoginView;
import com.vaadin.flow.spring.security.VaadinSecurityConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

// 14.7절 관리 화면(Vaadin) 보안 — 로그인 화면과 세션으로 지킨다
// /api/**는 SecurityConfig의 체인(@Order(1))이 먼저 가져가므로, 여기서는 그 밖의 모든 요청을 맡는다
@Configuration
@EnableWebSecurity
public class VaadinSecurityConfig {

    @Bean
    @Order(2)
    public SecurityFilterChain uiSecurity(HttpSecurity http) throws Exception {
        // Swagger UI 화면과 API 문서(JSON)는 로그인 없이 연다 (API 호출 자체는 Basic 인증이 필요하다)
        // /error는 오류 응답을 만드는 주소 — 막으면 원래 오류(예: API의 401)가 403으로 덮인다
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**", "/error").permitAll()
                // 20.6절 상태 확인(health)은 누구나, 나머지 Actuator는 관리자만
                .requestMatchers("/actuator/health").permitAll()
                .requestMatchers("/actuator/**").hasRole("ADMIN"));

        // Vaadin 전용 설정 — 로그인 화면 지정, Vaadin 내부 요청의 CSRF 처리, 화면별 접근 규칙(@PermitAll 등)
        http.with(VaadinSecurityConfigurer.vaadin(), vaadin -> vaadin.loginView(LoginView.class));
        return http.build();
    }
}
