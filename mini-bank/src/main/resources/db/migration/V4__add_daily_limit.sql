-- 13장 계좌별 1일 이체 한도 — 기존 계좌에도 기본값(300만 원)이 채워진다
ALTER TABLE account ADD COLUMN daily_limit DECIMAL(15, 2) NOT NULL DEFAULT 3000000;
