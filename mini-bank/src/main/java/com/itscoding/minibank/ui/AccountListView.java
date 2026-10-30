package com.itscoding.minibank.ui;

import com.itscoding.minibank.domain.TransactionType;
import com.itscoding.minibank.dto.AccountResponseDto;
import com.itscoding.minibank.dto.TransactionResponseDto;
import com.itscoding.minibank.service.AccountService;
import com.itscoding.minibank.service.CustomerService;
import com.itscoding.minibank.service.TransferFacade;
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
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
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

@Route(value = "accounts", layout = MainLayout.class)
public class AccountListView extends VerticalLayout {

    private final AccountService accountService;
    private final CustomerService customerService;
    private final TransferFacade transferFacade;   // 12.2절 이체 화면용
    private final Grid<AccountResponseDto> grid = new Grid<>(AccountResponseDto.class, false);
    private final NumberFormat won = NumberFormat.getIntegerInstance(Locale.KOREA);   // 30,000 형식

    public AccountListView(AccountService accountService, CustomerService customerService,
                           TransferFacade transferFacade) {
        this.accountService = accountService;
        this.customerService = customerService;
        this.transferFacade = transferFacade;

        // 9.10절 금액 서식(won 필드)
        grid.addColumn(AccountResponseDto::getAccountNumber).setHeader("계좌번호");
        grid.addColumn(AccountResponseDto::getOwnerName).setHeader("예금주").setSortable(true);
        grid.addColumn(new NumberRenderer<>(AccountResponseDto::getBalance, won))
            .setHeader("잔액(원)")
            .setTextAlign(ColumnTextAlign.END)
            .setComparator(AccountResponseDto::getBalance)
            .setSortable(true);
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
        grid.setWidthFull();

        // 12.5절 행을 클릭하면 거래내역 Dialog를 연다(12.7절 기간 검색, 12.8절 CSV 포함)
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
                new TransferFormDialog(accounts, transferFacade, this::refreshGrid);
        dialog.open();
    }

    private void openTransactionDialog(AccountResponseDto account) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle(account.getAccountNumber() + " 거래내역 (" + account.getOwnerName() + ")");

        Grid<TransactionResponseDto> txGrid = new Grid<>(TransactionResponseDto.class, false);

        // 9.10절 거래 구분을 한글과 색으로 — 입금은 초록, 출금은 빨강 (Lumo 테마 색 변수)
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

        // 날짜를 "yyyy-MM-dd HH:mm" 형태로 짧게
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        txGrid.addColumn(t -> t.getTransactedAt().format(formatter))
                .setHeader("거래일시").setAutoWidth(true).setFlexGrow(0);   // 내용 길이에 맞춰 폭 자동

        // 메모는 남는 공간을 다 쓰도록 flexGrow 부여 (폭 고정 X)
        txGrid.addColumn(TransactionResponseDto::getDescription)
                .setHeader("메모").setFlexGrow(1);

        txGrid.setWidthFull();
        txGrid.setAllRowsVisible(true);   // 데이터 개수만큼 높이 자동 조절

        // 12.7절 기간 선택 — 처음에는 이번 달 1일부터 오늘까지
        DatePicker fromPicker = new DatePicker("시작일", LocalDate.now().withDayOfMonth(1));
        DatePicker toPicker = new DatePicker("종료일", LocalDate.now());
        fromPicker.setLocale(Locale.KOREA);
        toPicker.setLocale(Locale.KOREA);
        Span summary = new Span();

        Runnable search = () -> {
            if (fromPicker.isEmpty() || toPicker.isEmpty()) {
                Notification.show("시작일과 종료일을 모두 골라 주세요.");
                return;
            }
            try {
                // 최신순으로 첫 페이지 20건만 가져온다 (API의 기본값과 같다)
                Page<TransactionResponseDto> page = accountService.searchTransactions(
                        account.getId(), fromPicker.getValue(), toPicker.getValue(),
                        PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC, "transactedAt")));
                txGrid.setItems(page.getContent());
                summary.setText("총 " + page.getTotalElements() + "건"
                        + (page.getTotalPages() > 1 ? " (최근 20건 표시)" : ""));
            } catch (IllegalArgumentException ex) {
                txGrid.setItems();                    // 이전 결과를 지워 혼동을 막는다
                summary.setText("");
                Notification.show(ex.getMessage());   // 시작일이 종료일보다 늦은 경우
            }
        };
        Button searchButton = new Button("조회", e -> search.run());

        HorizontalLayout filter = new HorizontalLayout(fromPicker, toPicker, searchButton, summary);
        filter.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.BASELINE);

        // 12.8절 CSV 내려받기 — /api 주소가 아니라 Vaadin이 직접 파일을 보낸다(14장 보안 뒤에도 그대로 동작)
        Anchor csvLink = new Anchor(DownloadHandler.fromInputStream(event -> {
            byte[] csv = accountService.exportTransactionsCsv(account.getId());   // API와 같은 메서드
            return new DownloadResponse(new ByteArrayInputStream(csv),
                    "transactions-" + account.getAccountNumber() + ".csv", "text/csv", csv.length);
        }), "CSV 내려받기");

        dialog.add(filter, txGrid);
        dialog.getFooter().add(csvLink, new Button("닫기", e -> dialog.close()));
        dialog.setWidth("900px");
        search.run();   // 열자마자 이번 달 거래를 보여 준다
        dialog.open();
    }
}
