package com.itscoding.minibank.ui;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

// 14.7절 직원 로그인 화면 — 메뉴(MainLayout) 없이 로그인 폼만 보여 준다
@Route("login")
@PageTitle("미니뱅크 직원 로그인")
@AnonymousAllowed                       // 로그인하기 전에도 열려야 하는 유일한 화면
public class LoginView extends VerticalLayout implements BeforeEnterObserver {

    private final LoginForm login = new LoginForm();

    public LoginView() {
        login.setAction("login");              // 폼을 POST /login 으로 보낸다 → Spring Security가 처리
        login.setForgotPasswordButtonVisible(false);
        login.setI18n(koreanI18n());

        add(new H1("미니뱅크 관리"), login);
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        // 로그인에 실패하면 Spring Security가 /login?error 로 돌려보낸다
        if (event.getLocation().getQueryParameters().getParameters().containsKey("error")) {
            login.setError(true);
        }
    }

    private LoginI18n koreanI18n() {
        LoginI18n i18n = LoginI18n.createDefault();
        i18n.getForm().setTitle("직원 로그인");
        i18n.getForm().setUsername("아이디");
        i18n.getForm().setPassword("비밀번호");
        i18n.getForm().setSubmit("로그인");
        i18n.getErrorMessage().setTitle("로그인 실패");
        i18n.getErrorMessage().setMessage("아이디 또는 비밀번호를 확인하세요.");
        return i18n;
    }
}
