---
name: mongo-query-analysis
description: MongoDB 쿼리 실행 계획 분석 스킬. explain('executionStats') 결과를 해석하고 COLLSCAN, 인덱스 미사용, 과다 docsExamined 등 병목 지점을 보고한다.
user-invocable: true
allowed-tools: Bash, Read, Grep
---

# /mongo-query-analysis — MongoDB 쿼리 실행 계획 분석

## 목적
입력된 MongoDB 쿼리의 실행 계획을 분석하여 병목 지점을 파악하고, 튜닝 스킬(`/mongo-query-tuning`)에 전달할 분석 리포트를 출력한다.

---

## 입력 형식

사용자가 분석할 MongoDB 쿼리를 제공한다 (find, aggregate 등).
explain 결과가 함께 제공되면 그것을 우선 사용하고, 없으면 아래 절차를 따른다.

---

## 실행 절차

### Step 1 — explain 명령 구성

```javascript
// find 쿼리 분석
db.collection.find({ field: value }).explain('executionStats');

// aggregate 파이프라인 분석
db.collection.explain('executionStats').aggregate([
  { $match: { field: value } },
  { $group: { _id: '$field', count: { $sum: 1 } } }
]);

// 가장 상세한 모드 (인덱스 선택 후보까지 확인)
db.collection.find({ field: value }).explain('allPlansExecution');
```

### Step 2 — 실행 계획 항목 점검

**winningPlan 분석**

| 항목 | 위험 신호 | 의미 |
|------|-----------|------|
| `stage` | `COLLSCAN` | 전체 컬렉션 스캔 (최악) |
| `stage` | `IXSCAN` | 인덱스 스캔 (양호) |
| `stage` | `FETCH` | 인덱스 후 도큐먼트 fetch |
| `stage` | `SORT_KEY_GENERATOR` | 인덱스 없는 정렬 (메모리 정렬) |
| `stage` | `PROJECTION_DEFAULT` | 프로젝션 적용 |

**executionStats 분석**

| 항목 | 위험 신호 | 의미 |
|------|-----------|------|
| `totalDocsExamined` ≫ `nReturned` | 비율이 높음 | 인덱스 효율 낮음 |
| `totalKeysExamined` ≫ `nReturned` | 비율이 높음 | 인덱스는 있으나 선택성 낮음 |
| `executionTimeMillis` | 높음 | 실행 시간 과다 |
| `memUsage` | 높음 | 메모리 정렬 발생 |
| `needsWork` | true | 추가 최적화 필요 |

**효율 비율 계산**
```
인덱스 효율 = nReturned / totalKeysExamined  (1에 가까울수록 좋음)
스캔 효율  = nReturned / totalDocsExamined   (1에 가까울수록 좋음)
```

### Step 3 — 쿼리 정적 분석

explain 없이도 아래 패턴을 쿼리에서 분석한다.

- `$where` 또는 JavaScript 표현식 사용 → 인덱스 무효, 보안 위험
- `$regex` 전방 앵커 없음 → 인덱스 미사용 (`/keyword/` vs `/^keyword/`)
- `$nin`, `$not` → 인덱스 효율 저하
- 정렬 필드가 인덱스에 없음 → 메모리 정렬 (32MB 제한)
- Aggregation `$lookup` 시 조인 컬렉션에 인덱스 없음
- `$group` 전에 `$match` 없음 → 전체 컬렉션 집계
- 불필요한 전체 도큐먼트 반환 (projection 없음)

### Step 4 — 분석 리포트 파일 생성

아래 경로에 Markdown 파일을 생성한다.

**저장 경로:** `docs/mongo/YYYY-MM-DD_analysis.md`
- `YYYY-MM-DD`는 오늘 날짜로 대체한다.
- 같은 날짜에 여러 분석이 있으면 `YYYY-MM-DD_analysis_2.md` 형태로 넘버링한다.
- `docs/mongo/` 디렉토리가 없으면 생성한다.

Write 툴을 사용하여 아래 형식으로 파일을 작성한다.

---

## 파일 내용 형식

```markdown
# MongoDB 쿼리 분석 결과

> 생성일: YYYY-MM-DD
> 튜닝 스킬: `/mongo-query-tuning`

## 대상 쿼리

\`\`\`javascript
[입력된 쿼리]
\`\`\`

## 실행 계획 요약

| 항목 | 값 |
|------|----|
| winningPlan stage | COLLSCAN / IXSCAN / FETCH |
| 사용된 인덱스 | [인덱스명 또는 없음] |
| totalDocsExamined | X |
| nReturned | Y |
| 스캔 효율 | Y/X (X%) |
| executionTimeMillis | Zms |

## 병목 지점

- 🔴/🟡/🔵 [항목]: [설명]

## 정적 분석

- [발견된 안티패턴 목록]

## 튜닝 필요 여부

- [ ] PASS
- [x] 튜닝 필요 → `/mongo-query-tuning` 실행 권장
```

파일 생성 후 사용자에게 저장 경로를 알린다.

---

## 주의사항
- `explain('executionStats')`는 실제 쿼리를 실행한다. 대용량 컬렉션 주의.
- 추측성 튜닝 제안은 이 스킬에서 작성하지 않는다. 분석 내용만 기록한다.
- 실제 튜닝은 `/mongo-query-tuning` 스킬을 사용한다.
