-- 개발 환경 전용: 재시작 시 스키마 초기화 (mode: always)
DROP TABLE IF EXISTS
    hp_identity_logs,
    hp_triage_records,
    hp_receptions,
    hp_patients,
    hp_staff,
    hp_departments,
    pre_consultations, reception_status_history, receptions,
    notification_log, symptom_department_mapping, audit_log,
    waiting_queue, staff, department, patient, kiosk,
    hospital_patient_mapping, visit_history, insurance_log
CASCADE;

-- =====================================================================
-- 진료과
-- =====================================================================
CREATE TABLE hp_departments (
    id   BIGSERIAL    PRIMARY KEY,
    code VARCHAR(20)  NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL
);

COMMENT ON TABLE  hp_departments      IS '진료과 목록. 원무과 승인 시 직원이 선택하는 기준 단위.';
COMMENT ON COLUMN hp_departments.code IS '진료과 코드 (예: INT=내과, ORT=정형외과)';
COMMENT ON COLUMN hp_departments.name IS '진료과 표시명 (한국어)';

-- =====================================================================
-- 원무과 직원 계정
-- =====================================================================
CREATE TABLE hp_staff (
    id         BIGSERIAL    PRIMARY KEY,
    username   VARCHAR(50)  NOT NULL UNIQUE,
    password   VARCHAR(255) NOT NULL,
    name       VARCHAR(100) NOT NULL,
    role       VARCHAR(20)  NOT NULL DEFAULT 'STAFF'
                            CHECK (role IN ('STAFF', 'ADMIN')),
    created_at TIMESTAMP    NOT NULL DEFAULT now()
);

COMMENT ON TABLE  hp_staff          IS '원무과 직원 계정. 대시보드 로그인 및 접수 승인 처리에 사용. password 는 BCrypt 해시로 저장.';
COMMENT ON COLUMN hp_staff.role     IS 'STAFF=일반 원무과 직원, ADMIN=관리자';
COMMENT ON COLUMN hp_staff.password IS 'BCrypt 해시. 평문 절대 저장 금지.';

-- =====================================================================
-- 환자
-- =====================================================================
CREATE TABLE hp_patients (
    id              BIGSERIAL    PRIMARY KEY,
    name            VARCHAR(100) NOT NULL,
    enc_rrn         TEXT,
    address         VARCHAR(255),
    phone           VARCHAR(20),
    last_visit_date DATE,
    created_at      TIMESTAMP    NOT NULL DEFAULT now(),
    updated_at      TIMESTAMP    NOT NULL DEFAULT now()
);

COMMENT ON TABLE  hp_patients                IS '병원 방문 환자 정보. enc_rrn 으로 동일 환자를 식별하며, last_visit_date 로 6개월 이내 재진 여부를 판별한다.';
COMMENT ON COLUMN hp_patients.enc_rrn        IS '주민등록번호 AES-256 CBC 암호화 저장값. 포맷: Base64(IV):Base64(CIPHERTEXT). 평문은 메모리에서만 처리.';
COMMENT ON COLUMN hp_patients.address        IS '환자 주소. HIS 연동 시 기본 인적 정보 전달 필드로 사용.';
COMMENT ON COLUMN hp_patients.phone          IS '연락처. 접수 완료 알림톡 발송에 사용.';
COMMENT ON COLUMN hp_patients.last_visit_date IS '가장 최근 접수 승인일. 6개월 초과 시 초진(신분증 확인 필요)으로 분류.';

-- =====================================================================
-- 접수
-- =====================================================================
CREATE TABLE hp_receptions (
    id             BIGSERIAL   PRIMARY KEY,
    patient_id     BIGINT      NOT NULL REFERENCES hp_patients(id),
    department_id  BIGINT      REFERENCES hp_departments(id),
    approved_by    BIGINT      REFERENCES hp_staff(id),
    visit_type     VARCHAR(10) NOT NULL
                               CHECK (visit_type IN ('FIRST', 'RETURN')),
    status         VARCHAR(20) NOT NULL DEFAULT 'SUBMITTED'
                               CHECK (status IN ('SUBMITTED', 'APPROVED', 'CANCELLED')),
    is_id_verified BOOLEAN     NOT NULL DEFAULT FALSE,
    id_verified_at TIMESTAMP,
    approved_at    TIMESTAMP,
    created_at     TIMESTAMP   NOT NULL DEFAULT now(),
    updated_at     TIMESTAMP   NOT NULL DEFAULT now()
);

CREATE INDEX idx_hp_receptions_status
    ON hp_receptions (status, created_at DESC);

CREATE INDEX idx_hp_receptions_patient
    ON hp_receptions (patient_id, created_at DESC);

CREATE UNIQUE INDEX uq_hp_receptions_patient_active
    ON hp_receptions (patient_id)
    WHERE status = 'SUBMITTED';

COMMENT ON TABLE  hp_receptions                IS '환자 1회 방문 접수 건. 환자 제출(SUBMITTED) → 직원 승인(APPROVED) 상태 흐름을 단일 레코드로 관리.';
COMMENT ON COLUMN hp_receptions.visit_type     IS 'FIRST=초진 또는 6개월 초과 재진(신분증 확인 필요), RETURN=6개월 이내 재진';
COMMENT ON COLUMN hp_receptions.status         IS 'SUBMITTED=환자 제출 완료 대기 중, APPROVED=직원 승인 완료, CANCELLED=취소';
COMMENT ON COLUMN hp_receptions.department_id  IS '직원 승인 시 선택한 진료과. 승인 전에는 NULL.';
COMMENT ON COLUMN hp_receptions.approved_by    IS '승인 처리한 직원 ID. 승인 전에는 NULL.';
COMMENT ON COLUMN hp_receptions.is_id_verified IS 'FIRST 방문 시 직원이 신분증 확인 완료 여부. FALSE 이면 접수 승인 불가.';
COMMENT ON COLUMN hp_receptions.id_verified_at IS '신분증 확인 완료 시각. 법적 근거 보존용.';

-- =====================================================================
-- 사전 문진
-- =====================================================================
CREATE TABLE hp_triage_records (
    id                BIGSERIAL    PRIMARY KEY,
    reception_id      BIGINT       NOT NULL UNIQUE REFERENCES hp_receptions(id) ON DELETE CASCADE,
    patient_id        BIGINT       NOT NULL REFERENCES hp_patients(id),
    main_symptom      VARCHAR(50),
    symptom_keywords  TEXT,
    pain_area         VARCHAR(100),
    pain_level        INT          CHECK (pain_level BETWEEN 0 AND 10),
    started_at_text   VARCHAR(100),
    free_text         TEXT,
    created_at        TIMESTAMP    NOT NULL DEFAULT now()
);

COMMENT ON TABLE  hp_triage_records                  IS '환자가 접수 시 작성한 사전 문진. 원무과 승인 전 직원이 미리 확인하는 진료 준비 데이터.';
COMMENT ON COLUMN hp_triage_records.main_symptom     IS '대표 증상 키워드 (예: 기침, 흉통)';
COMMENT ON COLUMN hp_triage_records.symptom_keywords IS '복수 증상 키워드 CSV (예: 기침,발열)';
COMMENT ON COLUMN hp_triage_records.pain_level       IS '통증 강도 0~10. 0=통증 없음, 10=극심한 통증.';
COMMENT ON COLUMN hp_triage_records.started_at_text  IS '환자 자유 입력 증상 시작 시점 (예: 어제 저녁부터)';
COMMENT ON COLUMN hp_triage_records.free_text        IS '환자가 의사에게 먼저 전달하고 싶은 자유 서술 내용';

-- =====================================================================
-- 신분증 확인 이력
-- =====================================================================
CREATE TABLE hp_identity_logs (
    id           BIGSERIAL PRIMARY KEY,
    reception_id BIGINT    NOT NULL REFERENCES hp_receptions(id),
    staff_id     BIGINT    NOT NULL REFERENCES hp_staff(id),
    verified_at  TIMESTAMP NOT NULL DEFAULT now()
);

COMMENT ON TABLE  hp_identity_logs             IS '직원이 신분증을 확인한 이력. 법적 본인 확인 의무 이행 근거로 보존.';
COMMENT ON COLUMN hp_identity_logs.staff_id    IS '신분증 확인을 수행한 직원 ID';
COMMENT ON COLUMN hp_identity_logs.verified_at IS '신분증 확인 수행 시각';

-- =====================================================================
-- 진료과 기초 데이터
-- =====================================================================
INSERT INTO hp_departments (code, name) VALUES
    ('INT', '내과'),
    ('SUR', '외과'),
    ('ORT', '정형외과'),
    ('PED', '소아과'),
    ('OBG', '산부인과'),
    ('DER', '피부과'),
    ('OPH', '안과'),
    ('ENT', '이비인후과'),
    ('NEU', '신경과'),
    ('PSY', '정신건강의학과'),
    ('URO', '비뇨의학과'),
    ('CAR', '심장내과'),
    ('EMR', '응급의학과');
