package com.itscoding.minibank.service;

import com.itscoding.minibank.domain.AuditLog;
import com.itscoding.minibank.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuditService {

    private final AuditLogRepository auditLogRepository;

    // 호출한 쪽의 트랜잭션과 별개로, 항상 새 트랜잭션에서 저장하고 바로 커밋한다
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void recordFailure(String action, String detail) {
        auditLogRepository.save(new AuditLog(action, detail, LocalDateTime.now()));
    }
}
