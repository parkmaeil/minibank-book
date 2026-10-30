package com.itscoding.minibank.dto;

import jakarta.validation.constraints.NotBlank;

public class CustomerUpdateRequestDto {

    @NotBlank(message = "수정할 고객 이름은 필수입니다.")
    private String name;
    private String phone;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
}
