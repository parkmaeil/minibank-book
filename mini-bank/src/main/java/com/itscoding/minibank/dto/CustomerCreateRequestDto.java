package com.itscoding.minibank.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomerCreateRequestDto {

    @NotBlank(message = "고객 이름은 필수입니다.")
    private String name;
    private String phone;
}
