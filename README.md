# springboot-jpa-template

JPA 기반 스프링부트 템플릿 프로젝트

## 주요 기술 스택

- **Java**: 17
- **Spring Boot**: 3.2.5
- **Gradle**: 8.2
- **MySQL**: 9.3.\*
- **Redis**
- **JWT**

---

## 프로젝트 구조

```
springboot-jpa-template
├─ src
│  ├─ main
│  │  ├─ java
│  │  │  └─ com.example.template
│  │  │      ├─ config         # 설정 파일 (DB, Redis, Security 등)
│  │  │      ├─ common         # 공통 클래스 (DTO, ...)
│  │  │      ├─ aop            # AOP 로그 관련 
│  │  │      ├─ model          # JPA 엔티티 ()
│  │  │      └─ util           # 유틸리티 클래스
│  │  │      └─ constants      # 상수 클래스
│  │  │      └─ ...            
│  │  └─ resources
│  │      ├─ application.yml
│  │      ├─ application-mac.yml
│  │      ├─ application-local.yml
│  │      ├─ application-dev.yml
│  │      └─ data.sql          # 초기 데이터
└─ build.gradle
```

---

## 빌드

### 1. 프로젝트 루트 경로 이동

### 2. Gradle Wrapper 설정 (최초 빌드 시)

```bash
./gradlew wrapper --gradle-version 8.2
```

### 3. 프로젝트 빌드

- **맥북**:

```bash
./gradlew clean build
```

- **윈도우**:

```bash
gradlew clean build
```

---

## Redis

- **윈도우 로컬 프로파일**: 임베디드 Redis 사용
- **맥북 로컬 프로파일**: 도커 Redis 사용

```bash
docker run --name redis-local -p 6380:6379 -d redis:latest
```

---

## DB

- **로컬 환경**: H2 인메모리 DB 사용
- **개발 환경**: MySQL 서버 사용

### 도커를 이용한 MySQL 실행

#### 맥북

```bash
docker run -d \
  --name mysql-dev \
  -e MYSQL_ROOT_PASSWORD=root_password \
  -e MYSQL_DATABASE=mydb \
  -p 3306:3306 \
  -v $(pwd)/data.sql:/docker-entrypoint-initdb.d/data-Dev.sql:ro \
  mysql:9.3
```

#### 윈도우

```powershell
docker run -d `
  --name mysql-dev `
  -e MYSQL_ROOT_PASSWORD=root_password `
  -e MYSQL_DATABASE=mydb `
  -p 3306:3306 `
  -v ${PWD}\data.sql:/docker-entrypoint-initdb.d/data-Dev.sql:ro `
  mysql:9.3
```

#### MySQL 접속

```bash
docker exec -it mysql-dev mysql -uroot -proot_password mydb
```

---

## H2 콘솔

- 로컬 H2 DB 사용 시 콘솔 접속 가능
- 경로: `/h2-console`
- JDBC URL: `jdbc:h2:mem:ymk`
- 사용자: `sa`
- 비밀번호: (없음)

---

## Spring 프로파일별 설정

| 프로파일    | DB      | Redis      |
| ------- | ------- | ---------- |
| mac     | H2 인메모리 | 도커 Redis   |
| local | H2 인메모리 | 임베디드 Redis |
| dev | MySQL | 도커 Redis |

> `application-mac.yml` / `application-local.yml` / `application-dev.yml` 참고

---

## JWT

- AccessToken
  - SecretKey: `acc_ess_tok_ens_ecr_et`
  - 유효기간: 30분
- RefreshToken
  - SecretKey: `ref_res_hto_ken_sec_ret`
  - 유효기간: 3일

---

## H2 초기 데이터 (data.sql 예제)

```sql
-- 사용자 생성
CREATE USER IF NOT EXISTS write_user PASSWORD 'write_password';
CREATE USER IF NOT EXISTS read_user PASSWORD 'read_password';
GRANT ALL ON SCHEMA PUBLIC TO write_user;
GRANT SELECT ON SCHEMA PUBLIC TO read_user;

-- 권한 레벨
INSERT INTO authority_level (level_code, description, created_by, updated_by) VALUES
('SUPER_ADMIN', '최고 관리자', 'SYSTEM', 'SYSTEM'),
('MID_ADMIN', '중간 관리자', 'SYSTEM', 'SYSTEM'),
('USER', '일반 사용자', 'SYSTEM', 'SYSTEM');

-- 권한 그룹
INSERT INTO authority_group (id, level_code, name, description, created_by, updated_by) VALUES
('authority_group_uuid1', 'SUPER_ADMIN', '최고관리자 그룹', '모든 메뉴 접근 가능', 'SYSTEM', 'SYSTEM'),
('authority_group_uuid2', 'MID_ADMIN', '중간관리자A', '메뉴1, 메뉴2 접근 가능', 'SYSTEM', 'SYSTEM'),
('authority_group_uuid3', 'MID_ADMIN', '중간관리자B', '메뉴3, 메뉴4 접근 가능', 'SYSTEM', 'SYSTEM'),
('authority_group_uuid4', 'USER', '일반사용자 그룹', '기본 메뉴만 접근 가능', 'SYSTEM', 'SYSTEM');

-- 메뉴
INSERT INTO menu (id, menu_name, path, sort_order, created_by, updated_by) VALUES
('menu_uuid1', '대시보드', '/dashboard', 1, 'SYSTEM', 'SYSTEM'),
('menu_uuid2', '회원관리', '/members', 2, 'SYSTEM', 'SYSTEM'),
('menu_uuid3', '권한관리', '/authorities', 3, 'SYSTEM', 'SYSTEM'),
('menu_uuid4', '메뉴관리', '/menus', 4, 'SYSTEM', 'SYSTEM');

-- 권한 그룹 메뉴 매핑
-- 최고관리자: 모든 메뉴
INSERT INTO authority_group_menu (id, group_id, menu_id, created_by, updated_by) VALUES
('authority_group_menu_uuid1', 'authority_group_uuid1', 'menu_uuid1', 'SYSTEM', 'SYSTEM'),
('authority_group_menu_uuid2', 'authority_group_uuid1', 'menu_uuid2', 'SYSTEM', 'SYSTEM'),
('authority_group_menu_uuid3', 'authority_group_uuid1', 'menu_uuid3', 'SYSTEM', 'SYSTEM'),
('authority_group_menu_uuid4', 'authority_group_uuid1', 'menu_uuid4', 'SYSTEM', 'SYSTEM');

-- 중간관리자A: 대시보드 + 회원관리
INSERT INTO authority_group_menu (id, group_id, menu_id, created_by, updated_by) VALUES
('authority_group_menu_uuid5', 'authority_group_uuid2', 'menu_uuid1', 'SYSTEM', 'SYSTEM'),
('authority_group_menu_uuid6', 'authority_group_uuid2', 'menu_uuid2', 'SYSTEM', 'SYSTEM');

-- 중간관리자B: 권한관리 + 메뉴관리
INSERT INTO authority_group_menu (id, group_id, menu_id, created_by, updated_by) VALUES
('authority_group_menu_uuid7', 'authority_group_uuid3', 'menu_uuid3', 'SYSTEM', 'SYSTEM'),
('authority_group_menu_uuid8', 'authority_group_uuid3', 'menu_uuid4', 'SYSTEM', 'SYSTEM');

-- 일반사용자: 대시보드만
INSERT INTO authority_group_menu (id, group_id, menu_id, created_by, updated_by) VALUES
('authority_group_menu_uuid9', 'authority_group_uuid4', 'menu_uuid1', 'SYSTEM', 'SYSTEM');

-- 관리자 계정
INSERT INTO admin (id, login_id, password, name, phone_number, email, authority_group_id, approval_status, created_by, updated_by) VALUES
('admin_uuid1', 'ymkmoon43', '$2a$10$tuLXB3HaKF9B6IkKQLkER.uBZkbP9qkcgIqUpXoXGrYDy1Ac3GiE2', '유명기', '01029320134', 'ymkmoon43@gmail.com', 'authority_group_uuid1', 'ACTIVE', 'SYSTEM', 'SYSTEM');
```

---

### JWT 인증 흐름

1. 로그인 시 AccessToken, RefreshToken 발급
2. AccessToken 만료 시 RefreshToken 으로 재발급
3. Spring Security Filter 에서 JWT 검증 후 요청 처리

---

## 참고

- H2 DB와 MySQL의 Dialect 차이를 고려
- 로컬 개발 시 프로파일별 DB/Redis 환경 주의
- 초기 데이터 파일(`data.sql`) 수정 가능

