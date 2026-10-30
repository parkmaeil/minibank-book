package com.itscoding.minibank.dto;

import com.itscoding.minibank.domain.Customer;

public class CustomerResponseDto {

    private Long id;
    private String name;
    private String phone;
    private long accountCount;

    public CustomerResponseDto() {
    }

    // 8.3절 JPQL의 SELECT new가 고객 정보와 계좌 수를 한 번에 담을 때 쓰는 생성자 (COUNT의 결과는 Long)
    public CustomerResponseDto(Long id, String name, String phone, Long accountCount) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.accountCount = accountCount;
    }

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

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public long getAccountCount() { return accountCount; }
    public void setAccountCount(long accountCount) { this.accountCount = accountCount; }
}
