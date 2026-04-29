# 🏥 Hospital Hyper-Pass

병원 원무과의 반복 수작업을 없애는 **셀프 체크인 + 사전 문진 + 원클릭 승인** 미들웨어입니다.

---

## 🤔 왜 만들었나요?

병원 원무과 창구에는 구조적인 병목이 있습니다.

- 환자는 매번 같은 정보를 종이에 직접 쓰고, 긴 줄을 서서 기다립니다.
- 직원은 신분 확인 → 진료과 배정 → 순번 발급을 환자마다 수작업으로 처리합니다.
- 초진·재진 구분이 수동이라 HIS 연계가 느리고 오류가 생깁니다.

**Hospital Hyper-Pass**는 환자가 QR을 스캔해 직접 인적사항과 사전 문진을 입력·제출하고, 원무과 직원은 신분증 대조 후 **원클릭 승인**만 하는 구조로 이 병목을 제거합니다.  
의사·간호사는 환자 입실 전에 문진 내용을 미리 확인할 수 있습니다.

> 카카오 인증서(CI) 기반 본인 확인은 HIS 연동 구축 후 도입 예정이며, 현재는 이름·전화번호 입력으로 환자를 식별합니다.

---

## ✨ 주요 기능

- 🙋 **환자 셀프 체크인**: 이름·전화번호 입력 → JWT 발급 → 사전 문진 제출
- 📋 **사전 문진**: 주요 증상, 복수 증상, 통증 부위·강도, 시작 시점, 자유 서술 입력
- 🔄 **초진/재진 자동 판별**: 최근 6개월 방문 이력 기반 서버 측 자동 결정
- 🖥️ **원무과 대시보드**: 승인 대기 목록 조회 → 신분증 확인 → 진료과 선택 → 원클릭 승인
- 🔒 **신분증 확인 강제**: 초진 환자는 신분증 미확인 시 서버에서 승인 거부
- 🔑 **이중 인증 구조**: 환자(이름+전화번호) / 직원(ID·PW) 각각 JWT 발급
- 🛡️ **주민번호 암호화**: AES-256 CBC 암호화 저장, 평문은 메모리에서만 처리

---

## 🛠️ 기술 스택

**Backend**

- Framework: Spring Boot 3.5.5, Java 17
- ORM: MyBatis 3.0.4 (JPA 미사용 — XML Mapper로 SQL 직접 제어)
- Database: PostgreSQL 16 (Neon Serverless)
- 인증: Spring Security + JWT (HS256)
- 암호화: AES-256 CBC
- 빌드: Maven
- 배포: Render (Docker)

**Frontend**

- Framework: Next.js / React 18 + TypeScript
- 빌드: Vite 5
- 스타일: Tailwind CSS v3
- 서버 상태: TanStack Query v5

---

## 🗄️ DB 테이블

| 테이블 | 역할 |
|--------|------|
| `hp_departments` | 진료과 목록 |
| `hp_staff` | 원무과 직원 계정 (BCrypt, STAFF / ADMIN 역할) |
| `hp_patients` | 환자 정보 (`enc_rrn` AES-256, `last_visit_date` 재진 판별) |
| `hp_receptions` | 접수 건 (`SUBMITTED / APPROVED / CANCELLED`, 신분증 확인 여부) |
| `hp_triage_records` | 사전 문진 (증상·통증·자유 서술) |
| `hp_identity_logs` | 신분증 확인 이력 (직원 ID, 확인 시각) — 법적 근거 보존 |

---

## 📡 주요 API

| 메서드 | 경로 | 설명 |
|--------|------|------|
| GET | `/health` | 헬스체크 |
| POST | `/api/auth/verify` | 이름+전화번호 → 환자 JWT 발급 |
| POST | `/api/admin/login` | 직원 ID·PW → 직원 JWT 발급 |
| POST | `/api/receptions/precheckin` | 사전 문진 제출 → SUBMITTED 생성 |
| GET | `/api/admin/receptions/pending` | 승인 대기 목록 조회 |
| PATCH | `/api/admin/receptions/{id}/verify-id` | 신분증 확인 완료 처리 |
| PATCH | `/api/admin/receptions/{id}/approve` | 접수 승인 (SUBMITTED → APPROVED) |

---

## 🚧 향후 계획

- **카카오 인증서(CI) 연동** — HIS 연동 구축 후 이름+전화번호 인증 대체
- **알림톡 실 API 연동** — 승인 시점 접수 완료 알림 자동 발송
- **WebSocket 대시보드** — 신규 접수 건 실시간 반영 (현재 폴링 방식)
- **Redis 대기 순번** — 원자적 순번 발급으로 동시성 보장
