package com.itscoding.minibank.repository;

import com.itscoding.minibank.domain.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

// JpaRepository<관리할 엔티티 타입, PK 타입>을 상속받으면 CRUD가 자동 완성된다
public interface CustomerRepository extends JpaRepository<Customer, Long> {
}
