-- 14.3절 직원 계정 — 비밀번호는 BCrypt 해시(60자)로만 저장한다
CREATE TABLE staff (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    username   VARCHAR(30)  NOT NULL UNIQUE,
    password   VARCHAR(100) NOT NULL,
    name       VARCHAR(50)  NOT NULL,
    role       ENUM('TELLER', 'ADMIN') NOT NULL,   -- 12장의 거래 구분처럼 Hibernate는 enum을 ENUM 타입으로 매핑한다
    enabled    BOOLEAN      NOT NULL,
    created_at DATETIME(6)  NOT NULL
);
