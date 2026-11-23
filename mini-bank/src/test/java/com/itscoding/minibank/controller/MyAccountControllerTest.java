package com.itscoding.minibank.controller;

import com.itscoding.minibank.auth.JwtConfig;
import com.itscoding.minibank.config.SecurityConfig;
import com.itscoding.minibank.dto.AccountResponseDto;
import com.itscoding.minibank.dto.TransferRequestDto;
import com.itscoding.minibank.service.AccountService;
import com.itscoding.minibank.service.TransferFacade;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// 17.3절 보안 체인(고객 JWT @Order(0) / 직원 Basic @Order(1))이 의도대로 나뉘는지 확인한다
@WebMvcTest(MyAccountController.class)
@Import({SecurityConfig.class, JwtConfig.class})
class MyAccountControllerTest {

    @Autowired MockMvc mockMvc;
    @MockitoBean AccountService accountService;
    @MockitoBean TransferFacade transferFacade;
    @MockitoBean UserDetailsService userDetailsService;   // 직원 조회(14.4절)는 가짜로

    @Test
    void 토큰_없이_내_계좌를_조회하면_401() throws Exception {
        mockMvc.perform(get("/api/me/accounts"))
               .andExpect(status().isUnauthorized());
    }

    @Test
    void 직원_Basic_인증으로는_고객_API에_접근할_수_없다() throws Exception {
        // /api/me/**는 고객용 체인이 먼저 잡으므로 Basic 헤더는 인증으로 인정되지 않는다
        mockMvc.perform(get("/api/me/accounts").with(httpBasic("teller", "teller1234!")))
               .andExpect(status().isUnauthorized());
    }

    @Test
    void CUSTOMER_토큰이면_자기_계좌_목록을_받는다() throws Exception {
        AccountResponseDto dto = new AccountResponseDto();
        dto.setAccountNumber("110-1111-2222");
        given(accountService.getAccountsOf(7L)).willReturn(List.of(dto));

        mockMvc.perform(get("/api/me/accounts")
                        .with(jwt().jwt(j -> j.claim("customerId", 7L))
                                   .authorities(new SimpleGrantedAuthority("SCOPE_CUSTOMER"))))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$[0].accountNumber").value("110-1111-2222"));
    }

    @Test
    void 고객_이체는_토큰의_customerId와_함께_TransferFacade로_보낸다() throws Exception {
        mockMvc.perform(post("/api/me/transfers")
                        .with(jwt().jwt(j -> j.claim("customerId", 7L))
                                   .authorities(new SimpleGrantedAuthority("SCOPE_CUSTOMER")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {"fromAccountNumber":"110-1111-2222","toAccountNumber":"110-3333-4444","amount":10000}
                            """))
               .andExpect(status().isOk());
        // 요청 본문이 아니라 토큰에서 꺼낸 7번 고객으로 소유권을 검사한다
        then(transferFacade).should().transferAsCustomer(eq(7L), any(TransferRequestDto.class));
    }
}
