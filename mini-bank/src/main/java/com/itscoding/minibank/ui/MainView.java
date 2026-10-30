package com.itscoding.minibank.ui;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

// http://localhost:8080/ (기본 경로)로 접속 시 이 화면이 렌더링된다
@Route(value = "", layout = MainLayout.class)
public class MainView extends VerticalLayout {
    public MainView() {
        H1 title = new H1("미니 금융 ERP");
        Paragraph desc = new Paragraph("고객과 계좌를 화면에서 관리합니다.");

        Button goToCustomers = new Button("고객 관리",
                e -> getUI().ifPresent(ui -> ui.navigate("customers")));
        Button goToAccounts = new Button("계좌 목록 보기",
                e -> getUI().ifPresent(ui -> ui.navigate("accounts")));

        add(title, desc, new HorizontalLayout(goToCustomers, goToAccounts));
        setAlignItems(Alignment.CENTER);   // 컴포넌트들을 화면 중앙에 정렬
    }
}
