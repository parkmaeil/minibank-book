CREATE TABLE interest_log (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    account_id BIGINT         NOT NULL,
    pay_date   DATE           NOT NULL,
    amount     DECIMAL(15, 2),
    CONSTRAINT uk_interest_account_date UNIQUE (account_id, pay_date)
);
