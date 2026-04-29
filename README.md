# Hospital Hyper-Pass

> 종이 없는 병원 접수 — 셀프 체크인 + 사전 문진 + 원무과 원클릭 승인

---

## 왜 만들게 되었는가

병원 원무과 창구에는 구조적인 병목이 있다.

- 환자는 매번 같은 정보를 종이에 직접 쓰고, 긴 줄을 서서 기다린다.
- 직원은 신분 확인 → 보험 자격 조회 → 진료과 배정 → 순번 발급을 환자마다 수작업으로 처리한다.
- 초진·재진 구분이 수동이라 HIS 연계가 느리고 오류가 생긴다.

Hospital Hyper-Pass는 이 흐름을 바꾸는 미들웨어다.

환자가 키오스크 QR을 스캔하면 **직접 인적사항과 사전 문진을 입력해 제출**하고, 원무과 직원은 신분증 대조 후 **원클릭으로 승인**만 한다. 직원이 반복 타이핑할 필요가 없고, 의사·간호사는 환자가 입실하기 전에 문진 내용을 미리 확인할 수 있다.

카카오 인증서 기반 CI 연동은 실제 HIS와의 연동 구축 후 도입할 예정이며, 현재는 HIS를 건드리지 않는 범위에서 이름·전화번호 입력으로 환자를 식별한다. 주민등록번호는 AES-256으로 암호화 저장하고, 평문은 메모리에서만 처리한다.

---

## 기술 스택

### Backend

| 항목 | 내용 |
|------|------|
| Language | Java 17 |
| Framework | Spring Boot 3.5.5 |
| ORM | MyBatis 3.0.4 (JPA 미사용 — XML Mapper로 SQL 직접 제어) |
| Database | PostgreSQL 16 (Neon Serverless) |
| 인증 | 이름 + 전화번호 입력 → JWT (HS256) / 직원 ID·PW → JWT |
| 암호화 | AES-256 CBC — `Base64(IV):Base64(CIPHERTEXT)` 단일 컬럼 저장 |
| 빌드 | Maven |
| 배포 | Render (Docker) |

### Frontend

| 항목 | 내용 |
|------|------|
| Framework | React 18 + TypeScript |
| 빌드 | Vite 5 |
| 스타일 | Tailwind CSS v3 |
| 서버 상태 | TanStack Query v5 |

---

## 현재 구현된 기능

### 환자 셀프 체크인

- 이름 + 전화번호 입력 → JWT 발급 (`POST /api/auth/verify`)
- 6개월 기준 초진/재진 자동 판별 — `last_visit_date` 서버 측 계산, 클라이언트 값 신뢰 안 함
- 인적사항 + 사전 문진(주요 증상, 복수 증상, 통증 부위·강도, 시작 시점, 자유 서술) 동시 제출 (`POST /api/receptions/precheckin`)
- 제출 즉시 `SUBMITTED` 상태로 생성 — 직원 승인 전까지 관제 미시작

### 원무과 대시보드

- 직원 로그인 → JWT 발급 (`POST /api/admin/login`)
- 승인 대기 목록 조회 (`GET /api/admin/receptions/pending`)
- 신분증 확인 완료 처리 + 이력 기록 (`PATCH /api/admin/receptions/{id}/verify-id`)
- 진료과 선택 후 접수 승인 (`PATCH /api/admin/receptions/{id}/approve`) — `SUBMITTED → APPROVED`
  - 초진 환자는 신분증 확인 완료 전 승인 서버에서 거부
  - 승인 시점에 `last_visit_date` 갱신 (다음 방문 재진 판별 기준)

### 보안 및 데이터 무결성

- 주민번호 AES-256 CBC 암호화 저장 (`enc_rrn` 컬럼, `CryptoTypeHandler` 자동 적용)
- 한 환자에 활성 상태 접수 2건 동시 생성 불가 (DB 레벨 유니크 제약)
- MyBatis `#{}` 바인딩 전용 — SQL Injection 방지
- 신분증 확인 이력 별도 테이블 보존 (`hp_identity_logs`) — 법적 근거 유지

### DB 테이블

| 테이블 | 역할 |
|--------|------|
| `hp_departments` | 진료과 목록 |
| `hp_staff` | 원무과 직원 계정 (BCrypt, STAFF / ADMIN 역할) |
| `hp_patients` | 환자 정보 (`enc_rrn` AES-256, `last_visit_date` 재진 판별) |
| `hp_receptions` | 접수 건 (`SUBMITTED / APPROVED / CANCELLED`, 신분증 확인 여부) |
| `hp_triage_records` | 사전 문진 (증상·통증·자유 서술) |
| `hp_identity_logs` | 신분증 확인 이력 (직원 ID, 확인 시각) |

---

## 향후 계획

- **카카오 인증서(CI) 연동** — HIS 연동 구축 후 이름+전화번호 인증을 대체
- **알림톡 실 API 연동** — 승인 시점 접수 완료 알림 자동 발송
- **WebSocket 대시보드** — 신규 접수 건 실시간 반영 (현재 폴링 방식)
