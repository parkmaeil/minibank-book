-- 15.5절 이자 지급 기록 — (계좌, 날짜)마다 한 번만
CREATE TABLE interest_log (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    account_id BIGINT         NOT NULL,
    pay_date   DATE           NOT NULL,
    amount     DECIMAL(15, 2),
    CONSTRAINT uk_interest_account_date UNIQUE (account_id, pay_date)
);
