package com.itscoding.minibank.ui;

import com.itscoding.minibank.dto.AccountResponseDto;
import com.itscoding.minibank.service.AccountService;
import com.itscoding.minibank.service.CustomerService;
import com.itscoding.minibank.service.TransferFacade;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.NumberRenderer;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.security.AuthenticationContext;
import jakarta.annotation.security.PermitAll;

import java.text.NumberFormat;
import java.util.Locale;

@Route(value = "accounts", layout = MainLayout.class)
@PermitAll   // 14.7절 로그인한 직원이면 누구나
public class AccountListView extends VerticalLayout {

    private final AccountService accountService;
    private final CustomerService customerService;
    private final TransferFacade transferFacade;   // 12.2절 이체 화면용
    private final boolean admin;                   // 14.8절 관리자인가 — 한도 변경 칸을 보여 줄지 정한다
    private final Grid<AccountResponseDto> grid = new Grid<>(AccountResponseDto.class, false);

    public AccountListView(AccountService accountService, CustomerService customerService,
                           TransferFacade transferFacade, AuthenticationContext authContext) {
        this.accountService = accountService;
        this.customerService = customerService;
        this.transferFacade = transferFacade;
        this.admin = authContext.hasRole("ADMIN");

        // 9.10절 금액 서식
        NumberFormat won = NumberFormat.getIntegerInstance(Locale.KOREA);
        grid.addColumn(AccountResponseDto::getAccountNumber).setHeader("계좌번호");
        grid.addColumn(AccountResponseDto::getOwnerName).setHeader("예금주").setSortable(true);
        grid.addColumn(new NumberRenderer<>(AccountResponseDto::getBalance, won))
            .setHeader("잔액(원)")
            .setTextAlign(ColumnTextAlign.END)
            .setComparator(AccountResponseDto::getBalance)
            .setSortable(true);
        // 13장 계좌마다 다른 1일 이체 한도를 목록에서도 바로 본다
        grid.addColumn(new NumberRenderer<>(AccountResponseDto::getDailyLimit, won))
            .setHeader("1일 한도(원)")
            .setTextAlign(ColumnTextAlign.END);
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
        grid.setWidthFull();

        // 13장 행을 클릭하면 계좌 상세 Dialog(한도 변경 + 12장의 거래내역·기간 검색·CSV)를 연다
        grid.addItemClickListener(event ->
                new AccountDetailDialog(event.getItem(), accountService, admin, this::refreshGrid).open());

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
                new TransferFormDialog(accounts, transferFacade, this::refreshGrid);
        dialog.open();
    }
}
