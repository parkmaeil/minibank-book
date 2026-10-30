CREATE TABLE audit_log (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    action     VARCHAR(50)  NOT NULL,
    detail     VARCHAR(500),
    created_at DATETIME(6)  NOT NULL
);
