package com.itscoding.minibank.ui;

import com.itscoding.minibank.domain.TransactionType;
import com.itscoding.minibank.dto.AccountResponseDto;
import com.itscoding.minibank.dto.TransactionResponseDto;
import com.itscoding.minibank.service.AccountService;
import com.itscoding.minibank.service.CustomerService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.NumberRenderer;
import com.vaadin.flow.router.Route;

import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
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

        // 12.5절 행을 클릭하면 거래내역 Dialog를 연다
        grid.addItemClickListener(event -> openTransactionDialog(event.getItem()));

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

    private void openTransactionDialog(AccountResponseDto account) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle(account.getAccountNumber() + " 거래내역");

        Grid<TransactionResponseDto> txGrid = new Grid<>(TransactionResponseDto.class, false);

        // 9.10절 거래 구분을 색으로 표시하기
        txGrid.addComponentColumn(t -> {
            Span label = new Span(t.getType() == TransactionType.DEPOSIT ? "입금" : "출금");
            label.getStyle().set("color", t.getType() == TransactionType.DEPOSIT
                    ? "var(--lumo-success-text-color)" : "var(--lumo-error-text-color)");
            return label;
        }).setHeader("구분").setWidth("90px").setFlexGrow(0);
        txGrid.addColumn(TransactionResponseDto::getAmount)
                .setHeader("금액").setWidth("110px").setFlexGrow(0);
        txGrid.addColumn(TransactionResponseDto::getBalanceAfter)
                .setHeader("거래후 잔액").setWidth("120px").setFlexGrow(0);

        // 🔥 날짜를 "yyyy-MM-dd HH:mm" 형태로 짧게 포맷팅
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        txGrid.addColumn(t -> t.getTransactedAt().format(formatter))
                .setHeader("거래일시").setWidth("150px").setFlexGrow(0);

        // 🔥 메모는 남는 공간을 다 쓰도록 flexGrow 부여 (폭 고정 X)
        txGrid.addColumn(TransactionResponseDto::getDescription)
                .setHeader("메모").setFlexGrow(1);

        txGrid.setItems(accountService.getTransactionHistory(account.getId()));
        txGrid.setWidthFull();
        txGrid.setAllRowsVisible(true);   // 데이터 개수만큼 높이 자동 조절

        dialog.add(txGrid);
        dialog.setWidth("900px");
        dialog.open();
    }
}
