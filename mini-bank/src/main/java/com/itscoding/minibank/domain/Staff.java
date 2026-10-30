package com.itscoding.minibank.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

// 14.3절 은행 직원 계정 — 관리 화면과 직원용 API에 로그인하는 사람
@Entity
@Table(name = "staff")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Staff {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 30)
    private String username;          // 로그인 아이디

    @Column(nullable = false, length = 100)
    private String password;          // BCrypt 해시 — 원래 비밀번호는 어디에도 저장하지 않는다

    @Column(nullable = false, length = 50)
    private String name;              // 화면에 보여 줄 직원 이름

    @Enumerated(EnumType.STRING)      // 순서 번호(0, 1)가 아니라 "TELLER" 같은 이름으로 저장
    @Column(nullable = false)
    private StaffRole role;

    @Column(nullable = false)
    private boolean enabled;          // 퇴사·휴직한 직원은 행을 지우지 않고 false로 바꿔 로그인만 막는다

    @Column(nullable = false)
    private LocalDateTime createdAt;

    public Staff(String username, String encodedPassword, String name, StaffRole role) {
        this.username = username;
        this.password = encodedPassword;
        this.name = name;
        this.role = role;
        this.enabled = true;
        this.createdAt = LocalDateTime.now();
    }

    public void disable() {
        this.enabled = false;
    }
}
