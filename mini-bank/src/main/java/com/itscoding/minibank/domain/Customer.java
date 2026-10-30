package com.itscoding.minibank.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Customer {
    private Long id;
    private String name;
    private String phone;
    private List<Account> accounts = new ArrayList<>();

    // 1) 기본 생성자
    public Customer() {
    }

    // 2) 전체 필드 생성자
    public Customer(Long id, String name, String phone, List<Account> accounts) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.accounts = accounts;
    }

    // 3) Getter / Setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public List<Account> getAccounts() { return accounts; }
    public void setAccounts(List<Account> accounts) { this.accounts = accounts; }

    // 4) equals() / hashCode() / toString()
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Customer customer = (Customer) o;
        return Objects.equals(id, customer.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Customer{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }

    public void addAccount(Account account) {
        this.accounts.add(account);
    }
}
