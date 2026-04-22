# Hyper-Pass 프로젝트 — CLAUDE.md

## 프로젝트 개요
스마트 병원 하이패스 접수 미들웨어. 카카오 인증서(CI) 기반으로 환자 대기시간을 줄이고 원무과 반복 업무를 자동화한다.
병원 기존 HIS(Hospital Information System)와는 REST API로 연동하며, 국민건강보험공단 API로 자격 조회를 수행한다.

---

## 기술 스택

| 항목 | 내용 |
|------|------|
| Language | Java 17 |
| Framework | Spring Boot 3.5.5 |
| ORM | MyBatis 3.0.4 (JPA 미사용 — XML Mapper로 SQL 완전 통제) |
| DB | PostgreSQL 16 |
| Cache / Queue | Redis 7 |
| 인증 | 카카오 인증서 (CI 기반) |
| 외부 API | 국민건강보험공단 API |
| 암호화 | AES-256 CBC (IV 별도 컬럼 저장) |
| 보안 | Spring Security, Jasypt |
| 비동기 | Spring @Async (알림톡 등) |
| 외부 HTTP | WebClient (타임아웃 3초 고정) |
| UI | Thymeleaf (모바일), jQuery + DataTables (관리자 대시보드) |
| 빌드 | Maven |
| 서버 포트 | 8080 (다른 프로젝트와 충돌 시 조정) |

---

## 핵심 설계 제약 (반드시 준수)

### MyBatis
- 파라미터 바인딩은 `#{}` 만 사용. `${}` 절대 금지 (SQL Injection 방지)
- 동적 SQL은 `<if>`, `<choose>`, `<foreach>` 활용
- Mapper XML 네임스페이스는 Mapper 인터페이스 FQCN과 반드시 일치

### 암호화
- 주민번호(SSN)는 AES-256 CBC 암호화 후 저장
- 암호화 IV는 `enc_ssn_iv` 컬럼에 별도 저장
- SSN 평문은 메모리에서만 처리, 로그/응답에 절대 노출 금지
- MyBatis `CryptoTypeHandler`로 암호화/복호화 자동화

### AuditLog
- INSERT 전용. UPDATE/DELETE 메서드 작성 금지
- SSN 및 민감 정보 AuditLog 기록 금지

### 대기열
- Redis `INCR`으로 원자적 순번 발급 → PostgreSQL 영구 저장

### 외부 API
- WebClient 타임아웃 3초 고정
- 알림톡 발송은 `@Async` 별도 스레드로 분리

---

## 패키지 구조

```
com.hyperpass
├── config/          ← MyBatis, Security, Redis, WebClient 설정
├── controller/      ← REST 엔드포인트
├── service/         ← 비즈니스 로직
├── mapper/          ← MyBatis Mapper 인터페이스
├── domain/          ← DB 엔티티 (MyBatis type alias 대상)
├── dto/             ← 요청/응답 DTO
├── exception/       ← 커스텀 예외 및 GlobalExceptionHandler
└── util/            ← AesUtil, CryptoTypeHandler 등
```

---

## DB 테이블 목록

| 테이블 | 설명 |
|--------|------|
| `patient` | 환자 정보 (enc_ssn, enc_ssn_iv 포함) |
| `hospital_patient_mapping` | 병원 HIS 환자번호 ↔ 미들웨어 CI 매핑 |
| `visit_history` | 내원 이력 |
| `waiting_queue` | 실시간 대기 순번 |
| `insurance_log` | 건강보험 자격 조회 이력 |
| `audit_log` | 시스템 감사 로그 (INSERT 전용) |
| `kiosk` | 키오스크 디바이스 정보 |

---

## 환자 프로세스

### 초진 (신규 환자)
```
QR 스캔 → 카카오 인증서 CI 획득 → 주민번호 뒷 7자리 입력
→ 건강보험공단 API 자격 조회 → AES-256 암호화 → DB 저장 → 대기 순번 발급
```

### 재진 (기존 환자)
```
QR 스캔 → 카카오 인증서 CI 획득 → 서버 자동 복호화/DB 조회
→ 병원 HIS 매핑 확인 → 진료과 직행 → 대기 순번 발급
```

---

## 개발 로드맵

### Phase 1 — 인프라 연동 및 MyBatis 초기 세팅
- MyBatis + PostgreSQL 연결 검증
- `application.yml` DB/MyBatis 설정 완성
- 환자(patient) 기초 CRUD XML Mapper + Interface 작성 및 연결 테스트

### Phase 2 — 보안: MyBatis 전용 암호화 TypeHandler 구현
- `AesUtil`: Java 17 기반 AES-256 CBC 암호화/복호화 유틸
- `CryptoTypeHandler`: SSN 필드 자동 암호화(SET)/복호화(GET) TypeHandler 등록

### Phase 3 — 환자 식별 및 병원 매핑 로직
- `hospital_patient_mapping` 테이블 설계 및 생성
- 초진/재진 구분 서비스 (`<if>`, `<choose>` 동적 SQL 활용)

### Phase 4 — 카카오 인증 및 외부 API 연동
- 카카오 인증서 CI 수신 REST 엔드포인트 설계
- 인증 결과 파싱 → Mapper 전달 Service Layer 구현

### Phase 5 — 건강보험공단 자격 조회 (Mock 전용)
- **실연동 불가**: 공단 API 사용에 요양기관번호 발급이 필요하므로 Mock으로 완전 대체
- `NhisApiClient` 인터페이스 정의 → `MockNhisApiClient` 구현체로 주입 (추후 실연동 시 구현체만 교체)
- Mock 응답: 공단 API 실제 규격(요청/응답 필드 구조) 기반으로 현실적인 가짜 데이터 반환
- 상태 코드 관리: `APPROVED`(하이패스 승인) / `INVALID`(창구 유도) / `PENDING`(수납 보류)
- 조회 결과 `insurance_log` 저장

### Phase 6 — 프론트엔드 (Thymeleaf + DataTables)
- 모바일 UI: QR 스캔 후 환자 정보 입력 화면 (Thymeleaf, 모바일 최적화)
- 관리자 대시보드: 실시간 대기 현황 + 자격 조회 결과 모니터링 (DataTables Server-side Processing)

### Phase 7 — Redis 기반 성능 최적화 및 배치 처리
- Redis Caching: 자격 조회 결과 및 대기 순번 캐싱 (DB 부하 감소)
- Morning Batch: Spring Scheduler로 당일 예약 환자 사전 자격 조회 (매일 새벽)

---

## 포트 충돌 주의
- 동일 환경에서 `kalisco_OCI` 프로젝트가 병행 실행 중일 수 있음
- `lsof -i :<port>` 로 포트 점유 확인 후 작업할 것
- kalisco_OCI 관련 프로세스 절대 종료하지 말 것
