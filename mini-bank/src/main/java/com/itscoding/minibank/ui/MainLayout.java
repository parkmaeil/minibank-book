package com.itscoding.minibank.ui;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.spring.security.AuthenticationContext;
import jakarta.annotation.security.PermitAll;
import org.springframework.security.core.userdetails.UserDetails;

@PermitAll   // 14.7절 화면을 감싸는 레이아웃도 접근 규칙이 필요하다 — 로그인한 직원이면 누구나
public class MainLayout extends AppLayout {

    public MainLayout(AuthenticationContext authContext) {   // 14.7절 로그인 정보를 주입받는다
        H1 title = new H1("미니뱅크 관리");
        title.getStyle().set("font-size", "1.2rem").set("margin", "0");

        // 14.7절 오른쪽 위: 로그인한 직원 아이디와 로그아웃 버튼
        String username = authContext.getAuthenticatedUser(UserDetails.class)
                .map(UserDetails::getUsername).orElse("");
        String role = authContext.hasRole("ADMIN") ? "관리자" : "창구 직원";
        Span user = new Span(username + " (" + role + ")");
        Button logout = new Button("로그아웃", e -> authContext.logout());
        HorizontalLayout userBox = new HorizontalLayout(user, logout);
        userBox.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        userBox.getStyle().set("margin-left", "auto").set("margin-right", "var(--lumo-space-m)");

        addToNavbar(new DrawerToggle(), title, userBox);  // 상단 바: 메뉴 열기 버튼 + 제목 + 로그인 정보

        SideNav nav = new SideNav();
        nav.addItem(new SideNavItem("홈", MainView.class));
        nav.addItem(new SideNavItem("고객 관리", CustomerListView.class));
        nav.addItem(new SideNavItem("계좌 목록", AccountListView.class));
        addToDrawer(nav);                                 // 왼쪽 서랍: 메뉴
    }
}
