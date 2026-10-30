CREATE TABLE customer (
    id    BIGINT AUTO_INCREMENT PRIMARY KEY,
    name  VARCHAR(50) NOT NULL,
    phone VARCHAR(20)
);

CREATE TABLE account (
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    account_number VARCHAR(20)    NOT NULL UNIQUE,
    balance        DECIMAL(15, 2) NOT NULL,
    opened_at      DATETIME(6)    NOT NULL,
    customer_id    BIGINT,
    version        BIGINT,
    CONSTRAINT fk_account_customer FOREIGN KEY (customer_id) REFERENCES customer (id)
);
