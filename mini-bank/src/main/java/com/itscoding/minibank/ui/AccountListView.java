package com.itscoding.minibank.ui;

import com.itscoding.minibank.dto.AccountResponseDto;
import com.itscoding.minibank.service.AccountService;
import com.itscoding.minibank.service.CustomerService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.NumberRenderer;
import com.vaadin.flow.router.Route;

import java.text.NumberFormat;
import java.util.Locale;

@Route(value = "accounts", layout = MainLayout.class)
public class AccountListView extends VerticalLayout {

    private final AccountService accountService;
    // 🔥 계좌 개설 Dialog에 고객 목록을 넘기기 위해 추가
    private final CustomerService customerService;
    private final Grid<AccountResponseDto> grid = new Grid<>(AccountResponseDto.class, false);

    public AccountListView(AccountService accountService, CustomerService customerService) {
        this.accountService = accountService;
        this.customerService = customerService;

        // 9.10절 금액 서식 — 30,000 형식, 오른쪽 정렬, 정렬 가능
        NumberFormat won = NumberFormat.getIntegerInstance(Locale.KOREA);
        grid.addColumn(AccountResponseDto::getAccountNumber).setHeader("계좌번호");
        grid.addColumn(AccountResponseDto::getOwnerName).setHeader("예금주").setSortable(true);
        grid.addColumn(new NumberRenderer<>(AccountResponseDto::getBalance, won))
            .setHeader("잔액(원)")
            .setTextAlign(ColumnTextAlign.END)
            .setComparator(AccountResponseDto::getBalance)
            .setSortable(true);
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);   // 줄무늬로 행 구분
        grid.setWidthFull();

        Button createButton = new Button("새 계좌 개설", e -> openCreateDialog());
        Button refreshButton = new Button("새로고침", e -> refreshGrid());
        Button toCustomers = new Button("고객 관리로 이동",
                e -> getUI().ifPresent(ui -> ui.navigate("customers")));

        add(new HorizontalLayout(createButton, refreshButton, toCustomers), grid);
        setSizeFull();
        refreshGrid();
    }

    private void refreshGrid() {
        grid.setItems(accountService.getAllAccounts());
    }

    private void openCreateDialog() {
        // 🔥 미리 고객 목록을 조회해서 Dialog에 넘겨줌 (ComboBox 채우기용)
        var customers = customerService.getAllCustomers();
        AccountFormDialog dialog =
                new AccountFormDialog(customers, accountService, this::refreshGrid);
        dialog.open();
    }
}
