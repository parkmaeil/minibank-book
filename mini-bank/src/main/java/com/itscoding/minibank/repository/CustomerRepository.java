package com.itscoding.minibank.repository;

import com.itscoding.minibank.domain.Customer;
import com.itscoding.minibank.dto.CustomerResponseDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    // 8.3절 고객 목록과 보유 계좌 수를 SQL 한 번으로 조회한다 (N+1 해결)
    // 계좌가 하나도 없는 고객도 목록에 남도록 LEFT JOIN을 쓰고, 계좌 수는 COUNT로 센다
    @Query("""
            SELECT new com.itscoding.minibank.dto.CustomerResponseDto(
                c.id, c.name, c.phone, COUNT(a))
            FROM Customer c LEFT JOIN Account a ON a.customer = c
            GROUP BY c.id, c.name, c.phone
            ORDER BY c.id
            """)
    List<CustomerResponseDto> findAllWithAccountCount();

    // 8.3절 6.8절의 이름 검색도 같은 방식으로 — 검색 결과마다 COUNT 쿼리가 나가지 않게 한다
    @Query("""
            SELECT new com.itscoding.minibank.dto.CustomerResponseDto(
                c.id, c.name, c.phone, COUNT(a))
            FROM Customer c LEFT JOIN Account a ON a.customer = c
            WHERE c.name LIKE CONCAT('%', :keyword, '%')
            GROUP BY c.id, c.name, c.phone
            ORDER BY c.name
            """)
    List<CustomerResponseDto> searchWithAccountCount(@Param("keyword") String keyword);

    // 19.2절 고객 로그인
    boolean existsByUsername(String username);

    Optional<Customer> findByUsername(String username);

    // 6.8절 쿼리 메서드 이름 규칙: WHERE name LIKE %keyword%
    List<Customer> findByNameContainingOrderByNameAsc(String keyword);

    // 같은 쿼리를 JPQL로 직접 쓰면
    @Query("SELECT c FROM Customer c WHERE c.name LIKE CONCAT('%', :keyword, '%') ORDER BY c.name")
    List<Customer> searchByName(@Param("keyword") String keyword);
}
