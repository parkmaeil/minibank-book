package com.itscoding.minibank.repository;

import com.itscoding.minibank.domain.Staff;
import com.itscoding.minibank.domain.StaffRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StaffRepository extends JpaRepository<Staff, Long> {

    // 14.4절 로그인할 때 아이디로 직원을 찾는다
    Optional<Staff> findByUsername(String username);

    // 14.5절 계정 준비 — 같은 아이디가 이미 있는가, 관리자가 한 명이라도 있는가
    boolean existsByUsername(String username);

    boolean existsByRole(StaffRole role);
}
