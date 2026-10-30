package com.itscoding.minibank.ui;

import com.itscoding.minibank.dto.CustomerCreateRequestDto;
import com.itscoding.minibank.service.CustomerService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;

public class CustomerFormDialog extends Dialog {

    public CustomerFormDialog(CustomerService customerService, Runnable onSaved) {
        setHeaderTitle("새 고객 등록");

        TextField nameField = new TextField("이름");
        TextField phoneField = new TextField("연락처");

        // 9.8절 Binder로 폼 검증
        Binder<CustomerCreateRequestDto> binder = new Binder<>(CustomerCreateRequestDto.class);
        binder.forField(nameField)
              .asRequired("이름은 필수입니다.")                          // 비어 있으면 필드 아래에 빨간 안내
              .withValidator(n -> n.length() <= 50, "이름은 50자 이하로 입력하세요.")
              .bind(CustomerCreateRequestDto::getName, CustomerCreateRequestDto::setName);
        binder.forField(phoneField)
              .withValidator(p -> p.isEmpty() || p.matches("\\d{2,3}-\\d{3,4}-\\d{4}"),
                             "010-1234-5678 형식으로 입력하세요.")
              .bind(CustomerCreateRequestDto::getPhone, CustomerCreateRequestDto::setPhone);

        Button saveButton = new Button("저장", e -> {
            CustomerCreateRequestDto dto = new CustomerCreateRequestDto();
            if (binder.writeBeanIfValid(dto)) {         // 모든 규칙을 통과했을 때만 dto에 값이 채워진다
                customerService.createCustomer(dto);
                onSaved.run();   // 부모 화면(CustomerListView)의 목록 새로고침 실행
                close();
            }
        });
        Button cancelButton = new Button("취소", e -> close());

        add(new FormLayout(nameField, phoneField));
        getFooter().add(cancelButton, saveButton);
    }
}
