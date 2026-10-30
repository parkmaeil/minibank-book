package com.itscoding.minibank.ui;

import com.itscoding.minibank.dto.CustomerResponseDto;
import com.itscoding.minibank.service.CustomerService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

@Route(value = "customers", layout = MainLayout.class)   // http://localhost:8080/customers
public class CustomerListView extends VerticalLayout {

    private final CustomerService customerService;
    private final Grid<CustomerResponseDto> grid = new Grid<>(CustomerResponseDto.class, false);

    public CustomerListView(CustomerService customerService) {
        this.customerService = customerService;

        grid.addColumn(CustomerResponseDto::getId)
                .setHeader("고객 ID").setWidth("100px").setFlexGrow(0);
        grid.addColumn(CustomerResponseDto::getName).setHeader("이름");
        grid.addColumn(CustomerResponseDto::getPhone).setHeader("연락처");
        grid.addColumn(CustomerResponseDto::getAccountCount).setHeader("보유 계좌 수");
        grid.setWidthFull();

        Button createButton = new Button("새 고객 등록", e -> openCreateDialog());
        Button refreshButton = new Button("새로고침", e -> refreshGrid());
        Button toAccounts = new Button("계좌 목록으로 이동",
                e -> getUI().ifPresent(ui -> ui.navigate("accounts")));

        add(new HorizontalLayout(createButton, refreshButton, toAccounts), grid);
        setSizeFull();
        refreshGrid();
    }

    private void refreshGrid() {
        grid.setItems(customerService.getAllCustomers());
    }

    private void openCreateDialog() {
        CustomerFormDialog dialog = new CustomerFormDialog(customerService, this::refreshGrid);
        dialog.open();
    }
}
