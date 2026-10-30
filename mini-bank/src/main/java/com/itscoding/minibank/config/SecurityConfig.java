package com.itscoding.minibank.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

// 직원이 누구인지는 StaffUserDetailsService(14.4절)가 staff 테이블에서 찾는다.
// UserDetailsService Bean이 하나뿐이면 Spring Security가 PasswordEncoder와 함께 자동으로 연결한다
@Configuration
@EnableMethodSecurity            // 14.8절 Service 메서드의 @PreAuthorize를 켠다
public class SecurityConfig {

    // 14.6절 직원용 REST API 보안 규칙
    @Bean
    @Order(1)                    // 관리 화면 체인(VaadinSecurityConfig, @Order(2))보다 먼저 검사
    public SecurityFilterChain apiSecurity(HttpSecurity http) throws Exception {
        http
            // 이 규칙은 /api/** 요청에만 적용 (나머지는 14.7절의 관리 화면 체인이 맡는다)
            .securityMatcher("/api/**")
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/interest/**").hasRole("ADMIN")          // 15.5절 이자 수동 실행은 관리자만
                .requestMatchers(HttpMethod.GET, "/api/**").hasAnyRole("TELLER", "ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/customers/**").hasRole("ADMIN")
                .requestMatchers("/api/**").hasAnyRole("TELLER", "ADMIN")
            )
            // Authorization 헤더로 인증
            .httpBasic(Customizer.withDefaults())
            // 17.4절 아래 corsConfigurationSource() 규칙을 적용
            .cors(Customizer.withDefaults())
            // 세션·쿠키를 쓰지 않는 API이므로
            .csrf(csrf -> csrf.disable())
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        return http.build();
    }

    // 17.4절 React 개발 서버(5173)에서 오는 요청을 허용한다
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:5173"));   // 허용할 출처를 정확히 나열
        config.setAllowedMethods(List.of("GET", "POST", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", config);
        return source;
    }

    // 14.4절 비밀번호는 BCrypt 해시로 저장하고 비교한다
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
