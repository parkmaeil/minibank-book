package com.itscoding.minibank.security;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

// 14.8절 지금 요청한 사람의 아이디 — API(Basic 인증)든 관리 화면(로그인 세션)이든 같은 방법으로 꺼낸다
public final class CurrentUser {

    private CurrentUser() {
    }

    public static String username() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth instanceof AnonymousAuthenticationToken) {
            return "system";          // 스케줄러(15장)처럼 사람이 아닌 실행
        }
        return auth.getName();
    }
}
