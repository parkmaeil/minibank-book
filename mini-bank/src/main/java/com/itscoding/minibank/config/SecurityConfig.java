package com.itscoding.minibank.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class SecurityConfig {

    // 14.3절 직원용 API 보안 규칙
    @Bean
    public SecurityFilterChain apiSecurity(HttpSecurity http) throws Exception {
        http
            // 이 규칙은 /api/** 요청에만 적용 (Vaadin 화면·Swagger UI는 대상이 아니다)
            .securityMatcher("/api/**")
            .authorizeHttpRequests(auth -> auth
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

    // 14.4절 비밀번호는 BCrypt 해시로 저장한다
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService users(PasswordEncoder encoder) {
        UserDetails teller = User.withUsername("teller")
                .password(encoder.encode("teller1234!"))
                .roles("TELLER")
                .build();
        UserDetails admin = User.withUsername("admin")
                .password(encoder.encode("admin1234!"))
                .roles("TELLER", "ADMIN")
                .build();
        return new InMemoryUserDetailsManager(teller, admin);
    }
}
