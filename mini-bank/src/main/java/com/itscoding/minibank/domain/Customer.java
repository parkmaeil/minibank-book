package com.itscoding.minibank.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 고객은 자신의 계좌 목록을 들고 있지 않는다.
// 계좌 → 고객 한 방향(N:1 단방향)만 매핑하고, 고객의 계좌는 AccountRepository로 조회한다 (6.6절)
@Entity
@Table(name = "customer")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)   // JPA 전용 기본 생성자
public class Customer {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(length = 20)
    private String phone;

    public Customer(String name, String phone) {
        this.name = name;
        this.phone = phone;
    }

    public void updateInfo(String name, String phone) {
        this.name = name;
        this.phone = phone;
    }
}
