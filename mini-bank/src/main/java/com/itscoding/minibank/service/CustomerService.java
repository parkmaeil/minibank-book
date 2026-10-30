package com.itscoding.minibank.service;

import com.itscoding.minibank.domain.Customer;
import com.itscoding.minibank.dto.CustomerCreateRequestDto;
import com.itscoding.minibank.dto.CustomerResponseDto;
import com.itscoding.minibank.dto.CustomerUpdateRequestDto;
import com.itscoding.minibank.exception.CustomerNotFoundException;
import com.itscoding.minibank.repository.AccountRepository;
import com.itscoding.minibank.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final AccountRepository accountRepository;      // 6.2절 계좌 수·보유 여부를 묻는다
    private final PasswordEncoder passwordEncoder;          // 19.2절

    @Transactional
    public Long createCustomer(CustomerCreateRequestDto request) {
        Customer customer = new Customer(request.getName(), request.getPhone());
        return customerRepository.save(customer).getId();
    }

    @Transactional(readOnly = true)
    public CustomerResponseDto getCustomer(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException(id));
        return CustomerResponseDto.from(customer, accountRepository.countByCustomerId(id));
    }

    @Transactional(readOnly = true)
    public List<CustomerResponseDto> getAllCustomers() {
        // 고객마다 COUNT 쿼리 → 고객과 계좌 수를 한 번에 : N+1 해결 (8장)
        return customerRepository.findAllWithAccountCount();
    }

    // 6.8절 이름 일부로 검색
    @Transactional(readOnly = true)
    public List<CustomerResponseDto> searchCustomers(String keyword) {
        if (keyword == null || keyword.isBlank()) return getAllCustomers();
        return customerRepository.searchWithAccountCount(keyword.trim());   // 8.3절
    }

    @Transactional
    public void updateCustomer(Long id, CustomerUpdateRequestDto request) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException(id));
        customer.updateInfo(request.getName(), request.getPhone());
        // save()를 호출하지 않아도 커밋 시점에 UPDATE가 실행된다 (6.3절)
    }

    // 19.2절 직원이 고객에게 로그인 계정을 발급한다
    @Transactional
    public void issueLogin(Long customerId, String username, String rawPassword) {
        if (customerRepository.existsByUsername(username)) {
            throw new IllegalStateException("이미 사용 중인 아이디입니다.");
        }
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException(customerId));
        customer.assignLogin(username, passwordEncoder.encode(rawPassword));
    }

    @Transactional
    public void deleteCustomer(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException(id));
        if (accountRepository.existsByCustomerId(id)) {        // 삭제 규칙은 Service가 명시적으로 지킨다
            throw new IllegalStateException("보유 계좌가 있는 고객은 삭제할 수 없습니다.");
        }
        customerRepository.delete(customer);
    }
}
