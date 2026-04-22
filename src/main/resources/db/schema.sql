-- 개발 환경 전용: 재시작 시 스키마 초기화 (mode: always)
DROP TABLE IF EXISTS
    symptom_department_mapping, audit_log, insurance_log,
    waiting_queue, visit_history, hospital_patient_mapping,
    department, patient, kiosk CASCADE;

-- =====================================================================
-- 키오스크 디바이스
-- =====================================================================
CREATE TABLE kiosk (
    id         BIGSERIAL    PRIMARY KEY,
    device_id  VARCHAR(64)  NOT NULL UNIQUE,
    location   VARCHAR(100),
    active     BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP    NOT NULL DEFAULT now()
);

COMMENT ON TABLE kiosk IS
    '병원 내 설치된 하이패스 키오스크 단말기 목록. 각 키오스크는 고유한 QR 생성 주체가 된다.';

COMMENT ON COLUMN kiosk.id         IS '키오스크 내부 식별자 (자동 증가)';
COMMENT ON COLUMN kiosk.device_id  IS '키오스크 고유 디바이스 ID (UUID 또는 시리얼 번호). QR 코드 생성 시 포함되어 어느 단말에서 접수했는지 추적';
COMMENT ON COLUMN kiosk.location   IS '키오스크 설치 위치 (예: 1층 로비, 외래 3층). 원무과 대시보드에서 접수 위치 표시에 사용';
COMMENT ON COLUMN kiosk.active     IS '운영 여부. false 이면 해당 단말에서 접수 불가 처리';
COMMENT ON COLUMN kiosk.created_at IS '키오스크 등록 일시';

-- =====================================================================
-- 진료과
-- =====================================================================
CREATE TABLE department (
    id   BIGSERIAL    PRIMARY KEY,
    code VARCHAR(20)  NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL
);

COMMENT ON TABLE department IS
    '병원 진료과 목록. 증상-진료과 매핑 및 대기 순번 발급의 기준 단위가 된다.';

COMMENT ON COLUMN department.id   IS '진료과 내부 식별자 (자동 증가)';
COMMENT ON COLUMN department.code IS '진료과 코드 (예: INT=내과, ORT=정형외과). 시스템 내부 식별 및 HIS 연동 키로 사용';
COMMENT ON COLUMN department.name IS '진료과 표시명 (한국어). 환자 UI 및 대시보드에 노출되는 이름';

-- =====================================================================
-- 환자
-- =====================================================================
CREATE TABLE patient (
    id             BIGSERIAL    PRIMARY KEY,
    ci_value       VARCHAR(128) NOT NULL UNIQUE,
    name           VARCHAR(100) NOT NULL,
    birth_date     DATE,
    gender         CHAR(1)      CHECK (gender IN ('M', 'F')),
    phone          VARCHAR(20),
    enc_ssn        VARCHAR(512),
    first_visit_at TIMESTAMP,
    last_visit_at  TIMESTAMP,
    created_at     TIMESTAMP    NOT NULL DEFAULT now(),
    updated_at     TIMESTAMP    NOT NULL DEFAULT now()
);

COMMENT ON TABLE patient IS
    '카카오 인증서로 본인확인된 환자 정보. 미들웨어의 핵심 엔티티이며 모든 접수·대기·보험 조회의 기준이 된다.';

COMMENT ON COLUMN patient.id             IS '환자 내부 식별자 (자동 증가). HIS 환자 번호와 별개로 미들웨어 전용으로 사용';
COMMENT ON COLUMN patient.ci_value       IS '카카오 인증서 CI(Connecting Information). 주민번호 기반으로 생성되는 고유 식별값으로, 환자를 초진/재진 구분하는 핵심 키. 평문 저장';
COMMENT ON COLUMN patient.name           IS '환자 성명 (카카오 인증서에서 수신)';
COMMENT ON COLUMN patient.birth_date     IS '환자 생년월일 (카카오 인증서에서 수신)';
COMMENT ON COLUMN patient.gender         IS '성별. M=남성, F=여성';
COMMENT ON COLUMN patient.phone          IS '환자 연락처. 알림톡 발송에 사용';
COMMENT ON COLUMN patient.enc_ssn        IS '주민등록번호 AES-256 CBC 암호화 저장값. 포맷: Base64(IV):Base64(CIPHERTEXT). CryptoTypeHandler가 입출력 시 자동 암/복호화';
COMMENT ON COLUMN patient.first_visit_at IS '미들웨어 기준 최초 접수 일시 (초진 시 기록)';
COMMENT ON COLUMN patient.last_visit_at  IS '미들웨어 기준 최근 접수 일시 (접수마다 갱신)';
COMMENT ON COLUMN patient.created_at     IS '레코드 최초 생성 일시';
COMMENT ON COLUMN patient.updated_at     IS '레코드 최종 수정 일시';

-- =====================================================================
-- 병원 HIS ↔ 미들웨어 환자 매핑
-- =====================================================================
CREATE TABLE hospital_patient_mapping (
    id             BIGSERIAL   PRIMARY KEY,
    patient_id     BIGINT      NOT NULL REFERENCES patient(id),
    his_patient_no VARCHAR(50) NOT NULL,
    hospital_code  VARCHAR(20),
    created_at     TIMESTAMP   NOT NULL DEFAULT now(),
    UNIQUE (patient_id, hospital_code)
);

COMMENT ON TABLE hospital_patient_mapping IS
    '미들웨어 환자(patient)와 병원 HIS(Hospital Information System) 환자 번호를 연결하는 매핑 테이블.
     한 환자가 여러 병원을 이용할 경우 hospital_code별로 레코드가 생성된다.';

COMMENT ON COLUMN hospital_patient_mapping.id             IS '매핑 내부 식별자';
COMMENT ON COLUMN hospital_patient_mapping.patient_id     IS '미들웨어 환자 ID (patient.id 참조)';
COMMENT ON COLUMN hospital_patient_mapping.his_patient_no IS '병원 HIS에서 부여한 환자 번호. REST API 연동 시 HIS 조회·등록의 키로 사용';
COMMENT ON COLUMN hospital_patient_mapping.hospital_code  IS '병원 식별 코드. 다기관 확장 시 병원별로 구분하기 위한 값';
COMMENT ON COLUMN hospital_patient_mapping.created_at     IS '매핑 생성 일시 (최초 HIS 연동 시점)';

-- =====================================================================
-- 내원 이력
-- =====================================================================
CREATE TABLE visit_history (
    id            BIGSERIAL   PRIMARY KEY,
    patient_id    BIGINT      NOT NULL REFERENCES patient(id),
    kiosk_id      BIGINT      REFERENCES kiosk(id),
    department_id BIGINT      REFERENCES department(id),
    visit_type    VARCHAR(10) NOT NULL CHECK (visit_type IN ('FIRST', 'RETURN')),
    status        VARCHAR(20) NOT NULL DEFAULT 'REGISTERED',
    visited_at    TIMESTAMP   NOT NULL DEFAULT now()
);

COMMENT ON TABLE visit_history IS
    '환자의 키오스크 접수 이력. 접수 시마다 1건이 생성되며 원무과 통계·이력 조회에 활용된다.';

COMMENT ON COLUMN visit_history.id            IS '내원 이력 식별자';
COMMENT ON COLUMN visit_history.patient_id    IS '접수 환자 ID (patient.id 참조)';
COMMENT ON COLUMN visit_history.kiosk_id      IS '접수에 사용된 키오스크 ID. NULL이면 직접 접수(비키오스크 경로)';
COMMENT ON COLUMN visit_history.department_id IS '방문 진료과. NULL이면 접수 당시 진료과 미결정 상태';
COMMENT ON COLUMN visit_history.visit_type    IS '방문 유형. FIRST=초진(CI 신규 등록), RETURN=재진(기존 CI 매칭)';
COMMENT ON COLUMN visit_history.status        IS '접수 처리 상태. REGISTERED=접수완료, CANCELLED=취소 등';
COMMENT ON COLUMN visit_history.visited_at    IS '접수 일시';

-- =====================================================================
-- 실시간 대기 순번
-- =====================================================================
CREATE TABLE waiting_queue (
    id            BIGSERIAL   PRIMARY KEY,
    patient_id    BIGINT      NOT NULL REFERENCES patient(id),
    department_id BIGINT      REFERENCES department(id),
    queue_number  INT         NOT NULL,
    status        VARCHAR(20) NOT NULL DEFAULT 'WAITING'
                              CHECK (status IN ('WAITING', 'CALLED', 'DONE', 'CANCELLED')),
    queued_at     TIMESTAMP   NOT NULL DEFAULT now(),
    called_at     TIMESTAMP,
    completed_at  TIMESTAMP
);

COMMENT ON TABLE waiting_queue IS
    '진료과별 실시간 대기 순번 테이블. Redis INCR으로 원자적 순번을 채번한 뒤 이 테이블에 영구 저장한다.
     원무과 대시보드의 호출·완료 처리, 환자 알림톡 발송의 기준이 된다.';

COMMENT ON COLUMN waiting_queue.id            IS '대기 레코드 식별자';
COMMENT ON COLUMN waiting_queue.patient_id    IS '대기 중인 환자 ID (patient.id 참조)';
COMMENT ON COLUMN waiting_queue.department_id IS '대기 진료과 ID. NULL이면 진료과 미배정 상태';
COMMENT ON COLUMN waiting_queue.queue_number  IS '진료과 내 대기 순번. Redis INCR으로 발급된 값을 저장';
COMMENT ON COLUMN waiting_queue.status        IS '대기 상태.
    WAITING  = 대기 중 (접수 후 호출 전),
    CALLED   = 호출됨 (원무과 또는 시스템이 환자 호출),
    DONE     = 진료 완료,
    CANCELLED= 대기 취소 (환자 이탈 등)';
COMMENT ON COLUMN waiting_queue.queued_at    IS '대기 등록 일시';
COMMENT ON COLUMN waiting_queue.called_at    IS '호출 일시. CALLED 상태 전환 시 기록';
COMMENT ON COLUMN waiting_queue.completed_at IS '완료 일시. DONE 상태 전환 시 기록';

-- =====================================================================
-- 건강보험 자격 조회 이력
-- =====================================================================
CREATE TABLE insurance_log (
    id           BIGSERIAL   PRIMARY KEY,
    patient_id   BIGINT      NOT NULL REFERENCES patient(id),
    status       VARCHAR(20) NOT NULL CHECK (status IN ('APPROVED', 'INVALID', 'PENDING')),
    raw_response TEXT,
    queried_at   TIMESTAMP   NOT NULL DEFAULT now()
);

COMMENT ON TABLE insurance_log IS
    '국민건강보험공단 자격 조회 결과 이력. 접수마다 조회하며 결과에 따라 접수 흐름이 분기된다.
     현재는 Mock API 응답을 저장하며, 요양기관번호 발급 후 실연동으로 전환한다.';

COMMENT ON COLUMN insurance_log.id           IS '조회 이력 식별자';
COMMENT ON COLUMN insurance_log.patient_id   IS '조회 대상 환자 ID (patient.id 참조)';
COMMENT ON COLUMN insurance_log.status       IS '자격 조회 결과.
    APPROVED = 자격 확인 완료 → 하이패스 접수 진행,
    INVALID  = 자격 이상 (급여 정지, 체납 등) → 창구 유도,
    PENDING  = 조회 지연 또는 응답 오류 → 수납 보류';
COMMENT ON COLUMN insurance_log.raw_response IS '공단 API 원본 응답 JSON. 장애 대응 및 감사 목적으로 보관 (SSN 등 민감 정보 포함 금지)';
COMMENT ON COLUMN insurance_log.queried_at   IS '자격 조회 요청 일시';

-- =====================================================================
-- 감사 로그
-- =====================================================================
CREATE TABLE audit_log (
    id         BIGSERIAL   PRIMARY KEY,
    actor_id   VARCHAR(64),
    action     VARCHAR(50) NOT NULL,
    target     VARCHAR(100),
    detail     TEXT,
    ip_address VARCHAR(45),
    created_at TIMESTAMP   NOT NULL DEFAULT now()
);

COMMENT ON TABLE audit_log IS
    '시스템 주요 행위에 대한 감사 로그. INSERT 전용 테이블로 UPDATE·DELETE 쿼리 작성 금지.
     의료 데이터 접근 이력 보존 및 보안 감사에 사용된다.';

COMMENT ON COLUMN audit_log.id         IS '감사 로그 식별자';
COMMENT ON COLUMN audit_log.actor_id   IS '행위 주체 식별자. 원무과 직원 ID 또는 시스템 프로세스 명';
COMMENT ON COLUMN audit_log.action     IS '수행된 행위 코드 (예: PATIENT_REGISTER, SSN_DECRYPT, QUEUE_CALL, INSURANCE_QUERY)';
COMMENT ON COLUMN audit_log.target     IS '행위 대상 (예: patient:42, waiting_queue:17). SSN 등 민감 정보 기록 금지';
COMMENT ON COLUMN audit_log.detail     IS '행위 부가 설명. 오류 메시지, 변경 전후 요약 등. SSN 기록 절대 금지';
COMMENT ON COLUMN audit_log.ip_address IS '요청 발신 IP 주소. IPv4(최대 15자) 및 IPv6(최대 45자) 지원';
COMMENT ON COLUMN audit_log.created_at IS '행위 발생 일시';

-- =====================================================================
-- 증상 → 진료과 매핑
-- =====================================================================
CREATE TABLE symptom_department_mapping (
    id            BIGSERIAL   PRIMARY KEY,
    keyword       VARCHAR(50) NOT NULL,
    department_id BIGINT      NOT NULL REFERENCES department(id),
    priority      INT         NOT NULL DEFAULT 1,
    UNIQUE (keyword, department_id)
);

COMMENT ON TABLE symptom_department_mapping IS
    '환자가 입력한 증상 키워드와 진료과를 연결하는 매핑 테이블.
     동일 키워드에 여러 진료과가 매핑될 수 있으며 priority 값이 낮을수록 우선 추천된다.';

COMMENT ON COLUMN symptom_department_mapping.id            IS '매핑 식별자';
COMMENT ON COLUMN symptom_department_mapping.keyword       IS '증상 키워드 (예: 기침, 골절, 흉통). 환자 UI 증상 선택 옵션과 일치해야 함';
COMMENT ON COLUMN symptom_department_mapping.department_id IS '매핑된 진료과 ID (department.id 참조)';
COMMENT ON COLUMN symptom_department_mapping.priority      IS '추천 우선순위. 값이 낮을수록 우선 표시 (1이 최우선)';

-- =====================================================================
-- 진료과 기초 데이터
-- =====================================================================
INSERT INTO department (code, name) VALUES
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

-- =====================================================================
-- 증상-진료과 기초 매핑 데이터
-- =====================================================================
INSERT INTO symptom_department_mapping (keyword, department_id, priority) VALUES
    ('기침',    (SELECT id FROM department WHERE code = 'INT'), 1),
    ('발열',    (SELECT id FROM department WHERE code = 'INT'), 1),
    ('복통',    (SELECT id FROM department WHERE code = 'INT'), 1),
    ('골절',    (SELECT id FROM department WHERE code = 'ORT'), 1),
    ('관절통',  (SELECT id FROM department WHERE code = 'ORT'), 1),
    ('피부발진',(SELECT id FROM department WHERE code = 'DER'), 1),
    ('눈충혈',  (SELECT id FROM department WHERE code = 'OPH'), 1),
    ('이통',    (SELECT id FROM department WHERE code = 'ENT'), 1),
    ('두통',    (SELECT id FROM department WHERE code = 'NEU'), 1),
    ('흉통',    (SELECT id FROM department WHERE code = 'CAR'), 1);
