package com.itscoding.minibank.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// 19.2절 직원이 고객 로그인 계정을 발급할 때 쓰는 요청
public class LoginIssueRequestDto {

    @NotBlank(message = "아이디는 필수입니다.")
    @Size(max = 30, message = "아이디는 30자 이하입니다.")
    private String username;

    @NotBlank(message = "비밀번호는 필수입니다.")
    @Size(min = 8, message = "비밀번호는 8자 이상입니다.")
    private String password;

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
