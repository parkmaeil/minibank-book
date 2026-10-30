package com.itscoding.minibank.ui;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;

public class MainLayout extends AppLayout {

    public MainLayout() {
        H1 title = new H1("미니뱅크 관리");
        title.getStyle().set("font-size", "1.2rem").set("margin", "0");
        addToNavbar(new DrawerToggle(), title);          // 상단 바: 메뉴 열기 버튼 + 제목

        SideNav nav = new SideNav();
        nav.addItem(new SideNavItem("홈", MainView.class));
        nav.addItem(new SideNavItem("고객 관리", CustomerListView.class));
        nav.addItem(new SideNavItem("계좌 목록", AccountListView.class));
        addToDrawer(nav);                                 // 왼쪽 서랍: 메뉴
    }
}
