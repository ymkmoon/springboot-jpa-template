---
name: mysql-query-analysis
description: MySQL 쿼리 실행 계획 분석 스킬. 입력된 SQL의 EXPLAIN 결과를 해석하고 병목 지점(Full Scan, 미사용 인덱스, filesort 등)을 보고한다.
user-invocable: true
allowed-tools: Bash, Read, Grep
---

# /mysql-query-analysis — MySQL 쿼리 실행 계획 분석

## 목적
입력된 MySQL SQL 쿼리의 실행 계획을 분석하여 병목 지점을 파악하고, 튜닝 스킬(`/mysql-query-tuning`)에 전달할 분석 리포트를 출력한다.

---

## 입력 형식

사용자가 분석할 SQL 쿼리를 제공한다.
EXPLAIN 결과가 함께 제공되면 그것을 우선 사용하고, 없으면 아래 절차를 따른다.

---

## 실행 절차

### Step 1 — EXPLAIN 명령 구성

아래 두 명령을 사용자에게 안내하거나, Bash 실행이 가능한 환경이면 직접 실행한다.

```sql
-- 기본 실행 계획
EXPLAIN FORMAT=JSON <사용자_쿼리>;

-- 실제 실행 통계 (MySQL 8.0.18+)
EXPLAIN ANALYZE <사용자_쿼리>;
```

### Step 2 — 실행 계획 항목 점검

아래 항목을 순서대로 점검한다.

| 항목 | 위험 신호 | 의미 |
|------|-----------|------|
| `type` | `ALL` | Full Table Scan |
| `type` | `index` | Full Index Scan (개선 여지 있음) |
| `key` | `NULL` | 인덱스 미사용 |
| `rows` | 예상 행 수가 실제보다 과다 | 통계 부정확 |
| `Extra` | `Using filesort` | 정렬을 인덱스로 처리 못함 |
| `Extra` | `Using temporary` | 임시 테이블 생성 (GROUP BY, DISTINCT) |
| `Extra` | `Using index` | Covering Index 적용 (양호) |
| `filtered` | 낮은 % | WHERE 조건 선택성 낮음 |

### Step 3 — 쿼리 정적 분석

EXPLAIN 결과 없이도 아래 패턴을 쿼리 텍스트에서 직접 분석한다.

- `SELECT *` 사용 여부 → 불필요한 컬럼 조회
- `WHERE` 절에 함수 적용 여부 → 인덱스 무력화 (`WHERE YEAR(created_at) = 2024`)
- `OR` 조건 남용 → 인덱스 병합 비효율
- `LIKE '%keyword'` → 전방 와일드카드로 인덱스 미사용
- `IN` 서브쿼리 vs `EXISTS` → 대용량 시 EXISTS 선호
- `LIMIT` 없는 전체 조회
- `JOIN` 시 ON 조건의 인덱스 여부

### Step 4 — 분석 리포트 파일 생성

아래 경로에 Markdown 파일을 생성한다.

**저장 경로:** `docs/mysql/YYYY-MM-DD_analysis.md`
- `YYYY-MM-DD`는 오늘 날짜로 대체한다.
- 같은 날짜에 여러 분석이 있으면 `YYYY-MM-DD_analysis_2.md` 형태로 넘버링한다.
- `docs/mysql/` 디렉토리가 없으면 생성한다.

Write 툴을 사용하여 아래 형식으로 파일을 작성한다.

---

## 파일 내용 형식

```markdown
# MySQL 쿼리 분석 결과

> 생성일: YYYY-MM-DD
> 튜닝 스킬: `/mysql-query-tuning`

## 대상 쿼리

\`\`\`sql
[입력된 SQL]
\`\`\`

## 실행 계획 요약

| 테이블 | type | key | rows | Extra |
|--------|------|-----|------|-------|
[EXPLAIN 결과 행]

## 병목 지점

- 🔴/🟡/🔵 [항목]: [설명]

## 정적 분석

- [발견된 안티패턴 목록]

## 튜닝 필요 여부

- [ ] PASS
- [x] 튜닝 필요 → `/mysql-query-tuning` 실행 권장
```

파일 생성 후 사용자에게 저장 경로를 알린다.

---

## 주의사항
- EXPLAIN 실행이 불가한 환경이면 정적 분석만 수행하고 파일에 명시한다.
- 추측성 튜닝 제안은 이 스킬에서 작성하지 않는다. 분석 내용만 기록한다.
- 실제 튜닝은 `/mysql-query-tuning` 스킬을 사용한다.
