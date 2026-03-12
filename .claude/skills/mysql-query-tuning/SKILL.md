---
name: mysql-query-tuning
description: MySQL 쿼리 튜닝 스킬. /mysql-query-analysis 결과를 기반으로 개선된 쿼리와 인덱스 전략을 제시하고, 예상 성능 향상 폭을 보고한다.
user-invocable: true
allowed-tools: Bash, Read, Grep
---

# /mysql-query-tuning — MySQL 쿼리 튜닝

## 목적
`/mysql-query-analysis`에서 도출된 병목 지점을 기반으로 개선된 SQL 쿼리와 인덱스 전략을 제시한다.
반드시 분석 결과가 먼저 있어야 한다. 분석 없이 튜닝 시작 금지.

---

## 전제 조건 (REQUIRED)

- `/mysql-query-analysis` 결과 또는 EXPLAIN 출력이 있어야 한다.
- 없으면 먼저 `/mysql-query-analysis`를 실행하도록 사용자에게 안내한다.

---

## 실행 절차

### Step 1 — 병목 원인 분류

분석 결과에서 아래 유형을 식별한다.

| 유형 | 원인 | 튜닝 방향 |
|------|------|-----------|
| Full Table Scan | 인덱스 없음 / 조건 부적절 | 인덱스 추가 |
| Using filesort | ORDER BY 컬럼 인덱스 없음 | 복합 인덱스 설계 |
| Using temporary | GROUP BY / DISTINCT 비효율 | 인덱스 or 쿼리 재작성 |
| 인덱스 미사용 | 함수 적용, 암묵적 형변환 | 조건 재작성 |
| N+1 패턴 | 반복 단건 쿼리 | JOIN or IN 절로 통합 |
| SELECT * | 불필요한 컬럼 조회 | 컬럼 명시 |

### Step 2 — 인덱스 전략 수립

아래 원칙에 따라 인덱스를 설계한다.

- **선택성(Selectivity) 우선**: 카디널리티 높은 컬럼을 인덱스 앞쪽에 배치
- **복합 인덱스**: `WHERE` + `ORDER BY` + `SELECT` 컬럼을 Covering Index로 구성
- **불필요한 인덱스 제거**: 쓰기 성능 저하 방지
- **Prefix Index**: `VARCHAR` 컬럼이 길 경우 앞 N자리만 인덱싱 검토
- **소프트 삭제 컬럼**: `isActive` 포함 복합 인덱스 권장 (이 프로젝트 규칙)

```sql
-- 복합 인덱스 예시
CREATE INDEX idx_table_col1_col2 ON table_name (col1, col2);

-- Covering Index 예시
CREATE INDEX idx_covering ON table_name (col1, col2, col3);
```

### Step 3 — 쿼리 재작성

병목 원인별 개선 쿼리를 작성한다.

**함수로 인한 인덱스 무력화 → 범위 조건으로 변환**
```sql
-- Before (인덱스 무효)
WHERE YEAR(created_at) = 2024

-- After (인덱스 유효)
WHERE created_at >= '2024-01-01' AND created_at < '2025-01-01'
```

**IN 서브쿼리 → EXISTS 또는 JOIN으로 변환**
```sql
-- Before
WHERE id IN (SELECT id FROM sub_table WHERE ...)

-- After
WHERE EXISTS (SELECT 1 FROM sub_table WHERE sub_table.id = main.id AND ...)
```

**LIKE 전방 와일드카드 → FULLTEXT INDEX 검토**
```sql
-- Before (인덱스 무효)
WHERE name LIKE '%keyword%'

-- After (FULLTEXT)
MATCH(name) AGAINST('keyword' IN BOOLEAN MODE)
```

### Step 4 — 튜닝 검증

개선 쿼리에 대해 EXPLAIN을 다시 실행하여 `type`이 `ALL`에서 개선됐는지 확인한다.

```sql
EXPLAIN FORMAT=JSON <개선된_쿼리>;
```

### Step 5 — 튜닝 결과 파일 생성 (REQUIRED)

아래 경로에 Markdown 파일을 생성한다.

**저장 경로:** `docs/mysql/YYYY-MM-DD_tuning.md`
- `YYYY-MM-DD`는 오늘 날짜로 대체한다.
- 같은 날짜에 여러 튜닝이 있으면 `YYYY-MM-DD_tuning_2.md` 형태로 넘버링한다.
- 분석 파일(`YYYY-MM-DD_analysis.md`)이 존재하면 파일 상단에 링크를 추가한다.
- `docs/mysql/` 디렉토리가 없으면 생성한다.

Write 툴을 사용하여 아래 형식으로 파일을 작성한다.

---

## 파일 내용 형식

```markdown
# MySQL 쿼리 튜닝 결과

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
| type | ALL | ref |
| rows | 100,000 | 12 |
| Extra | Using filesort | Using index |

## 예상 성능 향상

- 스캔 행 수: 100,000 → 12 (약 8,300배 감소)
- filesort 제거: 정렬 비용 제거
- 예상 응답 속도: 기존 대비 약 XX% 향상 *(추정 근거: EXPLAIN rows 기준)*

## 적용 시 주의사항

- [인덱스 추가로 인한 쓰기 성능 영향 등]
- 실제 성능은 데이터 분포와 서버 환경에 따라 다를 수 있음
```

파일 생성 후 사용자에게 저장 경로를 알린다.

---

## 주의사항
- 분석 없이 튜닝 금지. 반드시 `/mysql-query-analysis` 선행 실행.
- 성능 향상 폭은 EXPLAIN rows 기준 추정치임을 파일에 명시한다.
- 이 프로젝트의 Soft Delete 컬럼(`isActive`)은 복합 인덱스에 포함할 것.
