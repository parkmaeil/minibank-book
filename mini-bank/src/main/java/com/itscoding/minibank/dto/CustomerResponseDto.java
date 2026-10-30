package com.itscoding.minibank.dto;

import com.itscoding.minibank.domain.Customer;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomerResponseDto {

    private Long id;
    private String name;
    private String phone;
    private long accountCount;

    // 엔티티를 DTO로 옮겨 담는 정적 팩토리 메서드
    // Customer는 자신의 계좌 목록을 모르므로(N:1 단방향, 6.6절) 계좌 수는 Service가 세어서 넘겨준다
    public static CustomerResponseDto from(Customer customer, long accountCount) {
        CustomerResponseDto dto = new CustomerResponseDto();
        dto.id = customer.getId();
        dto.name = customer.getName();
        dto.phone = customer.getPhone();
        dto.accountCount = accountCount;
        return dto;
    }
}
