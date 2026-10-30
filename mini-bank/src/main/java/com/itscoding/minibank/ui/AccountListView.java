package com.itscoding.minibank.ui;

import com.itscoding.minibank.dto.AccountResponseDto;
import com.itscoding.minibank.service.AccountService;
import com.itscoding.minibank.service.CustomerService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.NumberRenderer;
import com.vaadin.flow.router.Route;

import java.text.NumberFormat;
import java.util.Locale;

@Route(value = "accounts", layout = MainLayout.class)
public class AccountListView extends VerticalLayout {

    private final AccountService accountService;
    private final CustomerService customerService;
    private final Grid<AccountResponseDto> grid = new Grid<>(AccountResponseDto.class, false);

    public AccountListView(AccountService accountService, CustomerService customerService) {
        this.accountService = accountService;
        this.customerService = customerService;

        // 9.10절 금액 서식
        NumberFormat won = NumberFormat.getIntegerInstance(Locale.KOREA);
        grid.addColumn(AccountResponseDto::getAccountNumber).setHeader("계좌번호");
        grid.addColumn(AccountResponseDto::getOwnerName).setHeader("예금주").setSortable(true);
        grid.addColumn(new NumberRenderer<>(AccountResponseDto::getBalance, won))
            .setHeader("잔액(원)")
            .setTextAlign(ColumnTextAlign.END)
            .setComparator(AccountResponseDto::getBalance)
            .setSortable(true);
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
        grid.setWidthFull();

        // 13장: 12.5절의 openTransactionDialog() 대신 계좌 상세 Dialog(한도 변경 + 거래내역)를 연다
        grid.addItemClickListener(event ->
                new AccountDetailDialog(event.getItem(), accountService, this::refreshGrid).open());

        Button createButton = new Button("새 계좌 개설", e -> openCreateDialog());
        Button transferButton = new Button("계좌 이체", e -> openTransferDialog());   // 🔥 12.2절
        Button refreshButton = new Button("새로고침", e -> refreshGrid());
        Button toCustomers = new Button("고객 관리로 이동",
                e -> getUI().ifPresent(ui -> ui.navigate("customers")));

        add(new HorizontalLayout(createButton, transferButton, refreshButton, toCustomers), grid);
        setSizeFull();
        refreshGrid();
    }

    private void refreshGrid() {
        grid.setItems(accountService.getAllAccounts());
    }

    private void openCreateDialog() {
        var customers = customerService.getAllCustomers();
        AccountFormDialog dialog =
                new AccountFormDialog(customers, accountService, this::refreshGrid);
        dialog.open();
    }

    private void openTransferDialog() {
        var accounts = accountService.getAllAccounts();
        if (accounts.size() < 2) {
            Notification.show("이체하려면 계좌가 최소 2개 이상 필요합니다.");
            return;
        }
        TransferFormDialog dialog =
                new TransferFormDialog(accounts, accountService, this::refreshGrid);
        dialog.open();
    }
}
