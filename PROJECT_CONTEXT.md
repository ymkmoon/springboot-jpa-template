# 📌 프로젝트 기본 정보

## 1. 프로젝트 이름
- JPA Template

## 2. 프로젝트 목표
- JPA, QueryDSL 의 Template 구현
- Spring Security + JWT 기반 인증/인가 구조 제공
- Multi-datasource (Master/Slave) 라우팅 구조 제공
- 최적화 및 성능 개선
- ORM 사용

---

# 🛠 기술 스택

## Backend
- Java 17
- Spring Boot 3.2.5
- Spring Security
- Spring Data JPA
- QueryDSL 5.0.0
- JJWT 0.12.6 (JWT 발급/검증)
- Spring AOP (로깅)
- Spring Actuator + Prometheus (모니터링)

## Database
- MySQL 8 (dev/prod 프로파일)
- H2 in-memory (mac/local 프로파일, MODE=MYSQL)

## Cache / Session
- Redis (액세스 토큰 저장)
- Embedded Redis (local 프로파일 한정)

## Build
- Gradle

---

# 📐 아키텍처 원칙

- **상태 관리 방식**: JWT 기반 Stateless. 세션 미사용(`SessionCreationPolicy.STATELESS`). 액세스 토큰은 Redis에, 리프레시 토큰은 DB에 저장.
- **API 설계 규칙**: Context path `/template`. 응답 형식은 `ApiResponse<T>` 통일 (`code`, `message`, `data`, `timestamp`). 비즈니스 오류는 `BusinessException(ResponseCode)` throw.
- **폴더 구조 규칙**: 도메인 단위 패키지 분리 (`auth`, `admin`, `redis`, `security`, `filter`, `config`, `model`, `exception`). 쿼리는 V3(QueryDSL) 우선 사용.
- **에러 처리 방식**: `GlobalExceptionHandler`에서 중앙 처리. JWT 오류는 `JwtRequestFilter`에서 직접 응답. 엔트리포인트 오류는 `JwtAuthenticationEntryPoint` / `JwtAccessDeniedHandler` 처리.
- **보안 정책**: JWT 필터(`JwtRequestFilter`)가 모든 요청을 사전 검증. 화이트리스트(`SecurityConstants`) 외 전체 인증 필수. 토큰은 Redis 저장값과 대조 검증. 로컬/dev 프로파일은 yml에 개발용 값 직접 설정. 운영(`prod`)은 배포 플랫폼 환경변수(`${ENV_VAR}`)로 주입 — yml에 기본값 없음.
- **성능 최적화 기준**: `@Transactional(readOnly = true)` → Read datasource 라우팅. `LazyConnectionDataSourceProxy`로 커넥션 지연 획득. QueryDSL fetch join으로 N+1 방지.

---

# 🧾 코딩 규칙 (반드시 준수)

- **네이밍 규칙**:
  - Entity: `*Entity` (예: `AdminEntity`), `BaseEntity` 상속
  - DTO: `*Dto` 클래스 내부의 정적 이너 클래스 (예: `AuthDto.SignInRequest`)
  - Service: 인터페이스 `*Service` + 구현체 `*ServiceImpl`
  - Repository: `*Repository` (JPA) + `*RepositoryCustom` (QueryDSL)
  - 상수: `*Constants` 클래스로 분리
- **주석 작성 원칙**: 클래스/메서드 상단에 목적·파라미터·반환값 주석. 자명한 코드에 불필요한 인라인 주석 지양.
- **테스트 작성 여부**: Y (현재 미흡, 개선 예정)
- **금지 사항**:
  - QueryDSL Q클래스(`src/main/generated/`) 직접 수정 금지
  - 신규 기능에 V1(Native SQL), V2(JPQL) 방식 사용 금지 → V3(QueryDSL) 사용
  - `prod` 프로파일의 JWT 시크릿·DB 비밀번호 yml 하드코딩 금지 → 반드시 배포 플랫폼 환경변수로 주입
  - `@Transactional(readOnly = true)` 메서드에서 쓰기 작업 금지

---

# 🚫 절대 변경 금지 사항

- `src/main/generated/` 하위 QueryDSL Q클래스 (빌드 시 자동 생성)
- `SecurityConstants`의 화이트리스트 구조 (보안 정책 변경 시 반드시 팀 검토 후 수정)
- `RoutingDataSource` 및 `DataSourceConfig`의 Master/Slave 분기 로직

---

# 📊 현재 진행 상태

## 완료된 작업
- Spring Security + JWT (AccessToken/RefreshToken) 인증 구조 구현
- Multi-datasource 라우팅 (Master 쓰기 / Slave 읽기)
- QueryDSL V3 기반 어드민 목록 API (V1 Native SQL, V2 JPQL 비교 구현 포함)
- Redis를 통한 액세스 토큰 저장 및 검증
- AOP 기반 서비스 레이어 로깅 (`LogAspect`)
- Spring Actuator + Prometheus 모니터링 엔드포인트 연동
- GlobalExceptionHandler 중앙 예외 처리
- JJWT 0.9.0 → 0.12.6 업그레이드 (API 마이그레이션 포함)
- 프로덕션 프로파일 생성 (`application-prod.yml` — 모든 민감값을 배포 플랫폼 환경변수로 주입)
- 로그아웃 엔드포인트 구현 (`POST /auth/sign-out` — Redis + DB 토큰 동시 삭제)
- 설정 방식: `.env` 파일 제거, Spring Boot profile 기반 yml 방식으로 통일 (로컬/dev는 yml 직접 설정, prod는 환경변수)

## 진행 중 작업
- 없음

## 다음 작업
- 로그인 브루트포스 방어 (Rate Limiting — bucket4j 또는 Spring Cloud Gateway)
- `OffsetBasedPageRequest.withPage()` null 반환 버그 수정
- `GlobalExceptionHandler` 반환 타입 불일치 정리 (`ApiResponse` vs `ResponseEntity<ApiResponse>`)
- 단위/통합 테스트 작성 (현재 커버리지 ~0%)
- Swagger / OpenAPI 문서 추가

---

# 🐛 알려진 이슈

- `OffsetBasedPageRequest.withPage(int pageNumber)` 가 `null` 반환 → NPE 위험
- `GlobalExceptionHandler` 일부 핸들러의 반환 타입 불일치 (일부는 `ApiResponse`, 일부는 `ResponseEntity<ApiResponse>`)
- `LogAspect` Pointcut 선언 메서드 내부 로그 구문이 실행되지 않음 (Pointcut 마커 메서드는 바디 실행 안 됨)
- HikariCP `maximum-pool-size: 500` (mac/local 프로파일) — 로컬 개발 환경에 과도한 설정
- `JwtRequestFilter`가 Spring Bean이 아닌 `new` 생성 방식 (`SecurityConfig` 내부)

---

# 🧠 Claude 작업 지침

Claude는 아래 원칙을 따를 것:

1. 기존 코드 스타일을 유지할 것
2. 불필요한 리팩토링은 하지 말 것
3. 변경 사항은 diff 형식으로 설명할 것
4. 파일 수정 시 이유를 반드시 설명할 것
5. 모호한 요구사항은 먼저 질문할 것
6. 객체 지향적으로 코딩할 것
7. 신규 쿼리는 V3(QueryDSL) 방식으로 작성할 것
8. 민감 정보(시크릿, 비밀번호)는 절대 하드코딩하지 말 것

---

# 📦 변경 로그 요약

## 2026-03-03
- 변경 내용:
  - JJWT 0.9.0 → 0.12.6 업그레이드 (`build.gradle`, `TokenProvider.java`, `JwtRequestFilter.java`)
  - `application-prod.yml` 신규 생성 (프로덕션 프로파일, 민감값 env var 필수, 기본값 없음)
  - `POST /auth/sign-out` 로그아웃 엔드포인트 구현 (`AuthService`, `AuthServiceImpl`, `AuthController`)
  - `.env` 파일 방식 제거 → Spring Boot profile 기반 yml 방식으로 통일
    - 로컬/dev 프로파일(`application.yml`, `-mac`, `-local`, `-dev`): yml에 개발용 값 직접 설정
    - 운영 프로파일(`application-prod.yml`): 배포 플랫폼 환경변수(`${ENV_VAR}`) 주입 방식 유지
    - `.env.example` 삭제
