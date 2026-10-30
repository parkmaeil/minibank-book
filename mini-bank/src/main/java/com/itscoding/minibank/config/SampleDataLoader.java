package com.itscoding.minibank.config;

import com.itscoding.minibank.dto.CustomerCreateRequestDto;
import com.itscoding.minibank.service.CustomerService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

// 3.5절에서 소개한 예제 — CustomerService가 생긴 6장부터 실제로 동작한다
@Component
@Profile("dev")                      // dev 프로파일에서만 Bean으로 등록된다
@RequiredArgsConstructor
public class SampleDataLoader {

    private final CustomerService customerService;

    @PostConstruct
    void load() {
        // 이미 데이터가 있으면 건너뜀
        if (!customerService.getAllCustomers().isEmpty()) return;
        for (String name : new String[]{"홍길동", "김민준", "이서연"}) {
            CustomerCreateRequestDto dto = new CustomerCreateRequestDto();
            dto.setName(name);
            dto.setPhone("010-0000-0000");
            customerService.createCustomer(dto);
        }
    }
}
