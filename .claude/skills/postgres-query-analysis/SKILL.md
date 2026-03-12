---
name: postgres-query-analysis
description: PostgreSQL 쿼리 실행 계획 분석 스킬. EXPLAIN ANALYZE 결과를 해석하고 Seq Scan, 비효율적 Join, 통계 부정확 등 병목 지점을 보고한다.
user-invocable: true
allowed-tools: Bash, Read, Grep
---

# /postgres-query-analysis — PostgreSQL 쿼리 실행 계획 분석

## 목적
입력된 PostgreSQL SQL 쿼리의 실행 계획을 분석하여 병목 지점을 파악하고, 튜닝 스킬(`/postgres-query-tuning`)에 전달할 분석 리포트를 출력한다.

---

## 입력 형식

사용자가 분석할 SQL 쿼리를 제공한다.
EXPLAIN 결과가 함께 제공되면 그것을 우선 사용하고, 없으면 아래 절차를 따른다.

---

## 실행 절차

### Step 1 — EXPLAIN 명령 구성

```sql
-- 실행 계획 + 실제 실행 통계 + 버퍼 사용량
EXPLAIN (ANALYZE, BUFFERS, FORMAT JSON) <사용자_쿼리>;

-- 읽기 전용 분석 (실제 실행 없이)
EXPLAIN (FORMAT JSON) <사용자_쿼리>;
```

> 주의: `EXPLAIN ANALYZE`는 실제로 쿼리를 실행한다. DML 쿼리는 트랜잭션으로 감싸거나 `EXPLAIN`만 사용한다.

### Step 2 — 실행 계획 항목 점검

| 항목 | 위험 신호 | 의미 |
|------|-----------|------|
| Node Type | `Seq Scan` | Full Table Scan |
| Node Type | `Hash Join` + 큰 Hash Batches | 메모리 부족으로 디스크 스필 |
| Node Type | `Nested Loop` + 대량 rows | N+1 패턴 |
| `actual rows` vs `rows` | 큰 괴리 | 통계(ANALYZE) 부정확 |
| `Buffers: hit` vs `read` | read 비율 높음 | 캐시 미스, 디스크 I/O 과다 |
| `cost` | 첫 번째 숫자(startup), 두 번째(total) | 총 비용 |
| `Filter` | rows removed 많음 | 인덱스로 커버 못하는 조건 |
| `Sort` | external merge | 정렬 메모리(`work_mem`) 부족 |

### Step 3 — 쿼리 정적 분석

EXPLAIN 없이도 아래 패턴을 쿼리 텍스트에서 분석한다.

- `SELECT *` 사용 → 불필요한 컬럼 조회
- `WHERE` 절에 함수 적용 → 인덱스 무력화 (`WHERE date_trunc('year', created_at) = '2024-01-01'`)
- `OR` 조건 → `UNION ALL`로 분리 검토
- `LIKE '%keyword'` → `pg_trgm` GIN 인덱스 검토
- 상관 서브쿼리(Correlated Subquery) → `JOIN` 또는 `LATERAL` 변환
- `DISTINCT` 과다 사용 → 쿼리 설계 재검토
- CTE 남용 (`WITH` 절이 최적화 경계 역할 — PostgreSQL 12 이전 특히 문제)

### Step 4 — 분석 리포트 파일 생성

아래 경로에 Markdown 파일을 생성한다.

**저장 경로:** `docs/postgres/YYYY-MM-DD_analysis.md`
- `YYYY-MM-DD`는 오늘 날짜로 대체한다.
- 같은 날짜에 여러 분석이 있으면 `YYYY-MM-DD_analysis_2.md` 형태로 넘버링한다.
- `docs/postgres/` 디렉토리가 없으면 생성한다.

Write 툴을 사용하여 아래 형식으로 파일을 작성한다.

---

## 파일 내용 형식

```markdown
# PostgreSQL 쿼리 분석 결과

> 생성일: YYYY-MM-DD
> 튜닝 스킬: `/postgres-query-tuning`

## 대상 쿼리

\`\`\`sql
[입력된 SQL]
\`\`\`

## 실행 계획 요약

[주요 노드 트리 요약 — 최대 10줄]

- 총 예상 비용: X
- 실제 실행 시간: Xms

## 병목 지점

- 🔴/🟡/🔵 [항목]: [설명]

## 통계 정확도

- rows 추정치 vs 실제치 괴리 여부: [있음/없음]
- ANALYZE 필요 여부: [예/아니오]

## 정적 분석

- [발견된 안티패턴 목록]

## 튜닝 필요 여부

- [ ] PASS
- [x] 튜닝 필요 → `/postgres-query-tuning` 실행 권장
```

파일 생성 후 사용자에게 저장 경로를 알린다.

---

## 주의사항
- EXPLAIN ANALYZE는 실제 쿼리를 실행하므로 DML 주의.
- 추측성 튜닝 제안은 이 스킬에서 작성하지 않는다. 분석 내용만 기록한다.
- 실제 튜닝은 `/postgres-query-tuning` 스킬을 사용한다.
