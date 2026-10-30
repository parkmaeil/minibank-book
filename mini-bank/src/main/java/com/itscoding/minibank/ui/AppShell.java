package com.itscoding.minibank.ui;

import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.theme.lumo.Lumo;

// Vaadin 25부터는 테마를 직접 지정한다. 이 책의 화면은 Lumo 테마 기준으로 작성했다
// (GridVariant.LUMO_ROW_STRIPES, --lumo-* 색상 변수 등)
@StyleSheet(Lumo.STYLESHEET)
public class AppShell implements AppShellConfigurator {
}
