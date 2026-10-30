package com.itscoding.minibank.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
public class TransferNotificationListener {

    @Async                                                          // 15.4절 별도 스레드에서 실행
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)   // 15.3절 커밋된 뒤에만
    public void onTransferCompleted(TransferCompletedEvent event) {
        // 실제 서비스라면 문자·푸시 발송 API를 호출하는 자리
        log.info("[알림] {} → {} {}원 이체 완료",
                event.fromAccountNumber(), event.toAccountNumber(), event.amount());
    }
}
