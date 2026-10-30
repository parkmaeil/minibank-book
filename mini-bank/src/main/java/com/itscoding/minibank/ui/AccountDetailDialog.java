package com.itscoding.minibank.ui;

import com.itscoding.minibank.domain.TransactionType;
import com.itscoding.minibank.dto.AccountResponseDto;
import com.itscoding.minibank.dto.TransactionResponseDto;
import com.itscoding.minibank.service.AccountService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.BigDecimalField;

import java.time.format.DateTimeFormatter;

public class AccountDetailDialog extends Dialog {

    public AccountDetailDialog(AccountResponseDto account,
                               AccountService accountService, Runnable onChanged) {
        setHeaderTitle(account.getAccountNumber() + " 계좌 상세");

        // 상단: 잔액·현재 한도·한도 변경
        Span balance = new Span("잔액: " + account.getBalance() + "원");
        Span limit = new Span("1일 이체 한도: " + account.getDailyLimit() + "원");
        BigDecimalField newLimit = new BigDecimalField("새 한도");
        Button change = new Button("한도 변경", e -> {
            try {
                AccountResponseDto updated =
                        accountService.updateDailyLimit(account.getId(), newLimit.getValue());
                limit.setText("1일 이체 한도: " + updated.getDailyLimit() + "원");
                Notification.show("한도가 변경되었습니다.");
                onChanged.run();
            } catch (Exception ex) {
                Notification.show("한도 변경 실패: " + ex.getMessage());
            }
        });
        HorizontalLayout summary = new HorizontalLayout(balance, limit, newLimit, change);
        summary.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.BASELINE);

        // 하단: 거래내역 (12.5절과 동일한 컬럼 구성)
        DateTimeFormatter f = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        Grid<TransactionResponseDto> txGrid = new Grid<>(TransactionResponseDto.class, false);
        txGrid.addComponentColumn(t -> {
            Span label = new Span(t.getType() == TransactionType.DEPOSIT ? "입금" : "출금");
            label.getStyle().set("color", t.getType() == TransactionType.DEPOSIT
                    ? "var(--lumo-success-text-color)" : "var(--lumo-error-text-color)");
            return label;
        }).setHeader("구분").setWidth("90px").setFlexGrow(0);
        txGrid.addColumn(TransactionResponseDto::getAmount).setHeader("금액");
        txGrid.addColumn(TransactionResponseDto::getBalanceAfter).setHeader("거래후 잔액");
        txGrid.addColumn(t -> t.getTransactedAt().format(f)).setHeader("거래일시");
        txGrid.addColumn(TransactionResponseDto::getDescription).setHeader("메모").setFlexGrow(1);
        txGrid.setItems(accountService.getTransactionHistory(account.getId()));
        txGrid.setAllRowsVisible(true);

        add(summary, txGrid);
        setWidth("900px");
    }
}
