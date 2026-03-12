---
name: postgres-query-tuning
description: PostgreSQL 쿼리 튜닝 스킬. /postgres-query-analysis 결과를 기반으로 인덱스 전략(B-tree, GIN, 부분 인덱스 등)과 개선된 쿼리를 제시하고, 예상 성능 향상 폭을 보고한다.
user-invocable: true
allowed-tools: Bash, Read, Grep
---

# /postgres-query-tuning — PostgreSQL 쿼리 튜닝

## 목적
`/postgres-query-analysis`에서 도출된 병목 지점을 기반으로 개선된 SQL 쿼리와 인덱스 전략을 제시한다.
반드시 분석 결과가 먼저 있어야 한다. 분석 없이 튜닝 시작 금지.

---

## 전제 조건 (REQUIRED)

- `/postgres-query-analysis` 결과 또는 EXPLAIN 출력이 있어야 한다.
- 없으면 먼저 `/postgres-query-analysis`를 실행하도록 사용자에게 안내한다.

---

## 실행 절차

### Step 1 — 병목 원인 분류

| 유형 | 원인 | 튜닝 방향 |
|------|------|-----------|
| Seq Scan | 인덱스 없음 | B-tree 인덱스 추가 |
| 함수 조건 인덱스 무효 | `WHERE f(col) = val` | 함수 기반 인덱스(Expression Index) |
| LIKE 패턴 검색 | `'%keyword%'` | GIN + pg_trgm 인덱스 |
| 배열/JSONB 검색 | 전체 스캔 | GIN 인덱스 |
| 큰 Hash Batches | 메모리 스필 | `work_mem` 조정 또는 쿼리 분리 |
| 통계 부정확 | rows 추정 오차 큼 | `ANALYZE table_name` 실행 |
| CTE 최적화 차단 | PostgreSQL 12 이전 | `MATERIALIZED` / 인라인화 |

### Step 2 — 인덱스 전략 수립

**B-tree 인덱스** (기본, 등치/범위 조건)
```sql
CREATE INDEX idx_table_col ON table_name (col);

-- 복합 인덱스 (선택성 높은 컬럼 앞)
CREATE INDEX idx_table_col1_col2 ON table_name (col1, col2);

-- 내림차순 인덱스 (ORDER BY col DESC)
CREATE INDEX idx_table_col_desc ON table_name (col DESC);
```

**부분 인덱스 (Partial Index)** — 소프트 삭제 패턴에 특히 유용
```sql
-- isActive = true 인 행만 인덱싱 (이 프로젝트 Soft Delete 규칙 적용)
CREATE INDEX idx_table_active ON table_name (col) WHERE is_active = true;
```

**함수 기반 인덱스 (Expression Index)**
```sql
-- Before: WHERE LOWER(email) = 'user@example.com' → Seq Scan
-- After:
CREATE INDEX idx_table_email_lower ON table_name (LOWER(email));
```

**GIN 인덱스** (텍스트 검색, JSONB, 배열)
```sql
-- pg_trgm 기반 LIKE 검색
CREATE EXTENSION IF NOT EXISTS pg_trgm;
CREATE INDEX idx_table_name_trgm ON table_name USING GIN (name gin_trgm_ops);

-- JSONB 검색
CREATE INDEX idx_table_data ON table_name USING GIN (data);
```

### Step 3 — 쿼리 재작성

**함수 조건 → 범위 조건**
```sql
-- Before
WHERE date_trunc('year', created_at) = '2024-01-01'

-- After
WHERE created_at >= '2024-01-01' AND created_at < '2025-01-01'
```

**상관 서브쿼리 → LEFT JOIN LATERAL**
```sql
-- Before (상관 서브쿼리, 행마다 실행)
SELECT *, (SELECT MAX(amount) FROM orders WHERE orders.user_id = u.id) FROM users u;

-- After
SELECT u.*, o.max_amount
FROM users u
LEFT JOIN LATERAL (
  SELECT MAX(amount) AS max_amount FROM orders WHERE user_id = u.id
) o ON true;
```

**CTE 최적화 (PostgreSQL 12+)**
```sql
-- Before (물리화로 최적화 차단)
WITH cte AS (SELECT ...)
SELECT * FROM cte WHERE ...;

-- After (인라인화 허용)
WITH cte AS NOT MATERIALIZED (SELECT ...)
SELECT * FROM cte WHERE ...;
```

### Step 4 — 설정 파라미터 점검

필요 시 아래 파라미터를 세션 또는 서버 수준에서 조정한다.

```sql
-- 정렬/해시 메모리 증가 (세션 레벨)
SET work_mem = '64MB';

-- 통계 업데이트
ANALYZE table_name;

-- 통계 정확도 향상 (특정 컬럼)
ALTER TABLE table_name ALTER COLUMN col SET STATISTICS 500;
```

### Step 5 — 튜닝 결과 파일 생성 (REQUIRED)

아래 경로에 Markdown 파일을 생성한다.

**저장 경로:** `docs/postgres/YYYY-MM-DD_tuning.md`
- `YYYY-MM-DD`는 오늘 날짜로 대체한다.
- 같은 날짜에 여러 튜닝이 있으면 `YYYY-MM-DD_tuning_2.md` 형태로 넘버링한다.
- 분석 파일(`YYYY-MM-DD_analysis.md`)이 존재하면 파일 상단에 링크를 추가한다.
- `docs/postgres/` 디렉토리가 없으면 생성한다.

Write 툴을 사용하여 아래 형식으로 파일을 작성한다.

---

## 파일 내용 형식

```markdown
# PostgreSQL 쿼리 튜닝 결과

> 생성일: YYYY-MM-DD
> 분석 리포트: [YYYY-MM-DD_analysis.md](./YYYY-MM-DD_analysis.md)

## 튜닝 전 쿼리

\`\`\`sql
[원본 SQL]
\`\`\`

## 튜닝 후 쿼리

\`\`\`sql
[개선된 SQL]
\`\`\`

## 인덱스 추가/변경

\`\`\`sql
[DDL 명령]
\`\`\`

## 실행 계획 비교

| 항목 | 튜닝 전 | 튜닝 후 |
|------|---------|---------|
| Node | Seq Scan | Index Scan |
| cost | 0..10000 | 0..8.5 |
| actual rows | 100,000 | 1 |

## 예상 성능 향상

- 스캔 비용: 10000 → 8.5 (약 1,200배 감소)
- 예상 응답 속도: 기존 대비 약 XX% 향상 *(추정 근거: EXPLAIN cost 기준)*

## 적용 시 주의사항

- [인덱스 추가로 인한 쓰기 성능 영향, VACUUM 주기 등]
- 실제 성능은 데이터 분포와 서버 환경에 따라 다를 수 있음
```

파일 생성 후 사용자에게 저장 경로를 알린다.

---

## 주의사항
- 분석 없이 튜닝 금지. 반드시 `/postgres-query-analysis` 선행 실행.
- `EXPLAIN ANALYZE`로 개선 전후 비교 후 적용 권장.
- 성능 향상 폭은 EXPLAIN cost 기준 추정치임을 파일에 명시한다.
- 부분 인덱스는 이 프로젝트의 `isActive` Soft Delete 패턴에 적극 활용한다.
