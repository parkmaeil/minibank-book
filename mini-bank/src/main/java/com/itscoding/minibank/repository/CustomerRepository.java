package com.itscoding.minibank.repository;

import com.itscoding.minibank.domain.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

// JpaRepository<관리할 엔티티 타입, PK 타입>을 상속받으면 CRUD가 자동 완성된다
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    // 6.8절 쿼리 메서드 이름 규칙: WHERE name LIKE %keyword%
    List<Customer> findByNameContainingOrderByNameAsc(String keyword);

    // 같은 쿼리를 JPQL로 직접 쓰면
    @Query("SELECT c FROM Customer c WHERE c.name LIKE CONCAT('%', :keyword, '%') ORDER BY c.name")
    List<Customer> searchByName(@Param("keyword") String keyword);
}
