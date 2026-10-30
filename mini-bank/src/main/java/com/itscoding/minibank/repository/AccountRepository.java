package com.itscoding.minibank.repository;

import com.itscoding.minibank.domain.Account;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class AccountRepository {

    private final Map<Long, Account> store = new ConcurrentHashMap<>();
    private final AtomicLong sequence = new AtomicLong(0);

    // 계좌 저장
    public Account save(Account account) {
        if (account.getId() == null) {
            account.setId(sequence.incrementAndGet());
        }
        store.put(account.getId(), account);
        return account;
    }

    // ID로 계좌 단건 조회
    public Optional<Account> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    // 전체 계좌 조회
    public List<Account> findAll() {
        return new ArrayList<>(store.values());
    }
}
