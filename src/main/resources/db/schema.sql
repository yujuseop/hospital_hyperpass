-- 개발 환경 전용: 재시작 시 스키마 초기화 (mode: always)
DROP TABLE IF EXISTS patient CASCADE;

CREATE TABLE patient (
    id             BIGSERIAL    PRIMARY KEY,
    ci_value       VARCHAR(128) NOT NULL UNIQUE,
    name           VARCHAR(100) NOT NULL,
    birth_date     DATE,
    gender         CHAR(1),
    phone          VARCHAR(20),
    enc_ssn        VARCHAR(512),
    enc_ssn_iv     VARCHAR(256),
    first_visit_at TIMESTAMP,
    last_visit_at  TIMESTAMP,
    created_at     TIMESTAMP    NOT NULL DEFAULT now(),
    updated_at     TIMESTAMP    NOT NULL DEFAULT now()
);
