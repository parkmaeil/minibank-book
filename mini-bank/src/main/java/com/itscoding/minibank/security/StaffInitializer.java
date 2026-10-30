package com.itscoding.minibank.security;

import com.itscoding.minibank.domain.Staff;
import com.itscoding.minibank.domain.StaffRole;
import com.itscoding.minibank.repository.StaffRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

// 14.5절 관리자 계정이 하나도 없으면 첫 관리자(admin)를 만든다
// 비밀번호는 코드가 아니라 설정에서 — 개발은 기본값, 운영은 ADMIN_PASSWORD 환경 변수(11.7절)
@Component
@RequiredArgsConstructor
@Slf4j
public class StaffInitializer implements ApplicationRunner {

    private final StaffRepository staffRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${minibank.staff.admin-password}")
    private String adminPassword;

    @Override
    public void run(ApplicationArguments args) {
        if (staffRepository.existsByRole(StaffRole.ADMIN)) {
            return;                                       // 이미 관리자가 있으면 아무것도 하지 않는다
        }
        staffRepository.save(new Staff("admin", passwordEncoder.encode(adminPassword),
                "관리자", StaffRole.ADMIN));
        log.info("초기 관리자 계정(admin)을 만들었습니다.");   // 비밀번호는 절대 로그에 남기지 않는다(3.8절)
    }
}
