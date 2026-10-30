package com.itscoding.minibank.ui;

import com.itscoding.minibank.domain.TransactionType;
import com.itscoding.minibank.dto.AccountResponseDto;
import com.itscoding.minibank.dto.TransactionResponseDto;
import com.itscoding.minibank.service.AccountService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.data.renderer.NumberRenderer;
import com.vaadin.flow.server.streams.DownloadHandler;
import com.vaadin.flow.server.streams.DownloadResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.io.ByteArrayInputStream;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

// 13장 계좌 상세 Dialog — 잔액·한도 확인과 한도 변경(위), 12장의 거래내역·기간 검색·CSV(아래)를 한 화면에
public class AccountDetailDialog extends Dialog {

    private final NumberFormat won = NumberFormat.getIntegerInstance(Locale.KOREA);   // 30,000 형식

    public AccountDetailDialog(AccountResponseDto account,
                               AccountService accountService, Runnable onChanged) {
        setHeaderTitle(account.getAccountNumber() + " 계좌 상세 (" + account.getOwnerName() + ")");

        // ── 위: 잔액·현재 한도·한도 변경
        Span balance = new Span("잔액: " + won.format(account.getBalance()) + "원");
        Span limit = new Span("1일 이체 한도: " + won.format(account.getDailyLimit()) + "원");
        BigDecimalField newLimit = new BigDecimalField("새 한도(원)");
        Button change = new Button("한도 변경", e -> {
            try {
                AccountResponseDto updated =
                        accountService.updateDailyLimit(account.getId(), newLimit.getValue());
                limit.setText("1일 이체 한도: " + won.format(updated.getDailyLimit()) + "원");
                newLimit.clear();
                Notification.show("한도가 변경되었습니다.");
                onChanged.run();                       // 목록의 한도 열도 새로 고친다
            } catch (Exception ex) {
                Notification.show("한도 변경 실패: " + ex.getMessage());   // 빈 값·0 이하는 Account가 막는다
            }
        });
        HorizontalLayout summary = new HorizontalLayout(balance, limit, newLimit, change);
        summary.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.BASELINE);

        // ── 아래: 12.5~12.8절의 거래내역 (구분 색, 원 단위, 기간 검색, CSV)
        Grid<TransactionResponseDto> txGrid = new Grid<>(TransactionResponseDto.class, false);
        txGrid.addComponentColumn(t -> {
            boolean deposit = t.getType() == TransactionType.DEPOSIT;
            Span label = new Span(deposit ? "입금" : "출금");
            label.getStyle().set("color", deposit
                    ? "var(--lumo-success-text-color)" : "var(--lumo-error-text-color)");
            return label;
        }).setHeader("구분").setWidth("80px").setFlexGrow(0);
        txGrid.addColumn(new NumberRenderer<>(TransactionResponseDto::getAmount, won))
                .setHeader("금액(원)").setTextAlign(ColumnTextAlign.END)
                .setWidth("110px").setFlexGrow(0);
        txGrid.addColumn(new NumberRenderer<>(TransactionResponseDto::getBalanceAfter, won))
                .setHeader("거래후 잔액(원)").setTextAlign(ColumnTextAlign.END)
                .setWidth("130px").setFlexGrow(0);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        txGrid.addColumn(t -> t.getTransactedAt().format(formatter))
                .setHeader("거래일시").setAutoWidth(true).setFlexGrow(0);
        txGrid.addColumn(TransactionResponseDto::getDescription)
                .setHeader("메모").setFlexGrow(1);
        txGrid.setWidthFull();
        txGrid.setAllRowsVisible(true);

        DatePicker fromPicker = new DatePicker("시작일", LocalDate.now().withDayOfMonth(1));
        DatePicker toPicker = new DatePicker("종료일", LocalDate.now());
        fromPicker.setLocale(Locale.KOREA);
        toPicker.setLocale(Locale.KOREA);
        Span count = new Span();

        Runnable search = () -> {
            if (fromPicker.isEmpty() || toPicker.isEmpty()) {
                Notification.show("시작일과 종료일을 모두 골라 주세요.");
                return;
            }
            try {
                Page<TransactionResponseDto> page = accountService.searchTransactions(
                        account.getId(), fromPicker.getValue(), toPicker.getValue(),
                        PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC, "transactedAt")));
                txGrid.setItems(page.getContent());
                count.setText("총 " + page.getTotalElements() + "건"
                        + (page.getTotalPages() > 1 ? " (최근 20건 표시)" : ""));
            } catch (IllegalArgumentException ex) {
                txGrid.setItems();                    // 이전 결과를 지워 혼동을 막는다
                count.setText("");
                Notification.show(ex.getMessage());
            }
        };
        Button searchButton = new Button("조회", e -> search.run());
        HorizontalLayout filter = new HorizontalLayout(fromPicker, toPicker, searchButton, count);
        filter.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.BASELINE);

        // CSV 내려받기 — API와 같은 Service 메서드(12.8절)
        Anchor csvLink = new Anchor(DownloadHandler.fromInputStream(event -> {
            byte[] csv = accountService.exportTransactionsCsv(account.getId());
            return new DownloadResponse(new ByteArrayInputStream(csv),
                    "transactions-" + account.getAccountNumber() + ".csv", "text/csv", csv.length);
        }), "CSV 내려받기");

        add(summary, filter, txGrid);
        getFooter().add(csvLink, new Button("닫기", e -> close()));
        setWidth("900px");
        search.run();   // 열자마자 이번 달 거래를 보여 준다
    }
}
