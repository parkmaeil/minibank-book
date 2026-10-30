package com.itscoding.minibank.ui;

import com.itscoding.minibank.dto.AccountResponseDto;
import com.itscoding.minibank.dto.TransferRequestDto;
import com.itscoding.minibank.service.TransferFacade;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.textfield.NumberField;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

// 12.2절 계좌 이체 화면 — API(7장)와 똑같이 TransferFacade를 거쳐 이체한다
public class TransferFormDialog extends Dialog {

    public TransferFormDialog(List<AccountResponseDto> accounts,
                              TransferFacade transferFacade, Runnable onSaved) {
        setHeaderTitle("계좌 이체");

        NumberFormat won = NumberFormat.getIntegerInstance(Locale.KOREA);   // 9.10절과 같은 금액 서식
        ComboBox<AccountResponseDto> fromBox = new ComboBox<>("보내는 계좌");
        ComboBox<AccountResponseDto> toBox = new ComboBox<>("받는 계좌");
        fromBox.setItems(accounts);
        toBox.setItems(accounts);
        fromBox.setItemLabelGenerator(a ->
                a.getAccountNumber() + " (" + a.getOwnerName() + ", 잔액 " + won.format(a.getBalance()) + "원)");
        toBox.setItemLabelGenerator(fromBox.getItemLabelGenerator());
        fromBox.setWidthFull();
        toBox.setWidthFull();

        NumberField amountField = new NumberField("이체 금액");
        amountField.setMin(0);

        Button saveButton = new Button("이체 실행", e -> {
            AccountResponseDto from = fromBox.getValue();
            AccountResponseDto to = toBox.getValue();
            Double amountValue = amountField.getValue();
            // 서버(AccountService)도 검사하지만, 화면에서 먼저 막으면 사용자에게 바로 알려 줄 수 있다
            if (from == null || to == null) {
                Notification.show("보내는 계좌와 받는 계좌를 모두 선택해주세요.");
                return;
            }
            if (from.getAccountNumber().equals(to.getAccountNumber())) {
                Notification.show("같은 계좌로는 이체할 수 없습니다.");
                return;
            }
            if (amountValue == null || amountValue <= 0) {
                Notification.show("이체 금액을 정확히 입력해주세요.");
                return;
            }
            TransferRequestDto request = new TransferRequestDto();
            request.setFromAccountNumber(from.getAccountNumber());
            request.setToAccountNumber(to.getAccountNumber());
            request.setAmount(BigDecimal.valueOf(amountValue));
            try {
                transferFacade.transferWithRetry(request);   // 충돌 재시도 + 실패 기록까지 API와 동일
                Notification.show("이체가 완료되었습니다.");
                onSaved.run();
                close();
            } catch (Exception ex) {
                Notification.show("이체 실패: " + ex.getMessage());
            }
        });
        Button cancelButton = new Button("취소", e -> close());

        add(new FormLayout(fromBox, toBox, amountField));
        getFooter().add(cancelButton, saveButton);
    }
}
