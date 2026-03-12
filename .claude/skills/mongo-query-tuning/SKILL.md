---
name: mongo-query-tuning
description: MongoDB 쿼리 튜닝 스킬. /mongo-query-analysis 결과를 기반으로 복합 인덱스, 커버링 인덱스, Aggregation 파이프라인 최적화 전략을 제시하고, 예상 성능 향상 폭을 보고한다.
user-invocable: true
allowed-tools: Bash, Read, Grep
---

# /mongo-query-tuning — MongoDB 쿼리 튜닝

## 목적
`/mongo-query-analysis`에서 도출된 병목 지점을 기반으로 인덱스 전략과 개선된 쿼리를 제시한다.
반드시 분석 결과가 먼저 있어야 한다. 분석 없이 튜닝 시작 금지.

---

## 전제 조건 (REQUIRED)

- `/mongo-query-analysis` 결과 또는 explain 출력이 있어야 한다.
- 없으면 먼저 `/mongo-query-analysis`를 실행하도록 사용자에게 안내한다.

---

## 실행 절차

### Step 1 — 병목 원인 분류

| 유형 | 원인 | 튜닝 방향 |
|------|------|-----------|
| COLLSCAN | 인덱스 없음 | 단일/복합 인덱스 추가 |
| 메모리 정렬 | 정렬 필드 인덱스 없음 | 정렬 방향 포함 복합 인덱스 |
| 낮은 스캔 효율 | 인덱스 선택성 낮음 | 복합 인덱스 설계 개선 |
| FETCH 과다 | 프로젝션 없음 | Covered Query(커버링 인덱스) |
| $lookup 느림 | 조인 컬렉션 인덱스 없음 | 로컬/외래 필드 인덱스 추가 |
| $regex 비효율 | 전방 앵커 없음 | `^` 앵커 추가 또는 Text Index |

### Step 2 — 인덱스 전략 수립

**단일 인덱스**
```javascript
db.collection.createIndex({ field: 1 });          // 오름차순
db.collection.createIndex({ field: -1 });         // 내림차순
```

**복합 인덱스 (ESR 원칙)**
```
E(Equality) → S(Sort) → R(Range) 순으로 배치
```
```javascript
// WHERE status = 'ACTIVE' ORDER BY createdAt DESC RANGE: amount > 100
db.collection.createIndex({ status: 1, createdAt: -1, amount: 1 });
```

**커버링 인덱스 (Covered Query)**
```javascript
// 쿼리에서 반환하는 모든 필드를 인덱스에 포함
db.collection.createIndex({ status: 1, name: 1, email: 1 });

// 도큐먼트 fetch 없이 인덱스만으로 결과 반환
db.collection.find(
  { status: 'ACTIVE' },
  { _id: 0, name: 1, email: 1 }   // projection 필수
).hint({ status: 1, name: 1, email: 1 });
```

**부분 인덱스 (Partial Index)** — Soft Delete 패턴
```javascript
// isActive: true 도큐먼트만 인덱싱
db.collection.createIndex(
  { field: 1 },
  { partialFilterExpression: { isActive: { $eq: true } } }
);
```

**Text Index** (전문 검색)
```javascript
db.collection.createIndex({ name: 'text', description: 'text' });

// 사용
db.collection.find({ $text: { $search: 'keyword' } });
```

**TTL Index** (만료 데이터 자동 삭제)
```javascript
db.collection.createIndex(
  { createdAt: 1 },
  { expireAfterSeconds: 86400 }
);
```

### Step 3 — 쿼리 재작성

**COLLSCAN → IXSCAN**
```javascript
// Before (인덱스 없음)
db.orders.find({ status: 'PENDING' });

// After (인덱스 추가 후)
db.orders.createIndex({ status: 1, createdAt: -1 });
db.orders.find({ status: 'PENDING' }).sort({ createdAt: -1 });
```

**$regex 최적화**
```javascript
// Before (인덱스 불가)
db.users.find({ name: /keyword/ });

// After (전방 앵커, 인덱스 활용)
db.users.find({ name: /^keyword/ });

// 또는 Text Index 사용
db.users.find({ $text: { $search: 'keyword' } });
```

**Aggregation 파이프라인 최적화**
```javascript
// Before ($match 늦게 실행 → 전체 도큐먼트 집계)
db.orders.aggregate([
  { $group: { _id: '$userId', total: { $sum: '$amount' } } },
  { $match: { total: { $gt: 100 } } }
]);

// After ($match 먼저 → 집계 대상 도큐먼트 축소)
db.orders.aggregate([
  { $match: { status: 'COMPLETED' } },   // 인덱스 활용
  { $group: { _id: '$userId', total: { $sum: '$amount' } } },
  { $match: { total: { $gt: 100 } } }
]);
```

**$lookup 최적화**
```javascript
// Before (외래 컬렉션 인덱스 없음)
db.orders.aggregate([
  { $lookup: {
    from: 'users',
    localField: 'userId',
    foreignField: '_id',
    as: 'user'
  }}
]);

// After (users._id에 인덱스 확보 후 pipeline으로 조건 추가)
// users._id는 기본 인덱스 존재, 필요 시:
db.users.createIndex({ _id: 1, name: 1, email: 1 }); // 커버링
```

### Step 4 — 튜닝 검증

```javascript
// 개선 쿼리에 대해 explain 재실행
db.collection.find({ ... }).explain('executionStats');

// 확인 항목
// - stage: IXSCAN (COLLSCAN에서 변경)
// - nReturned ≈ totalDocsExamined
// - executionTimeMillis 감소
```

### Step 5 — 튜닝 결과 파일 생성 (REQUIRED)

아래 경로에 Markdown 파일을 생성한다.

**저장 경로:** `docs/mongo/YYYY-MM-DD_tuning.md`
- `YYYY-MM-DD`는 오늘 날짜로 대체한다.
- 같은 날짜에 여러 튜닝이 있으면 `YYYY-MM-DD_tuning_2.md` 형태로 넘버링한다.
- 분석 파일(`YYYY-MM-DD_analysis.md`)이 존재하면 파일 상단에 링크를 추가한다.
- `docs/mongo/` 디렉토리가 없으면 생성한다.

Write 툴을 사용하여 아래 형식으로 파일을 작성한다.

---

## 파일 내용 형식

```markdown
# MongoDB 쿼리 튜닝 결과

> 생성일: YYYY-MM-DD
> 분석 리포트: [YYYY-MM-DD_analysis.md](./YYYY-MM-DD_analysis.md)

## 튜닝 전 쿼리

\`\`\`javascript
[원본 쿼리]
\`\`\`

## 튜닝 후 쿼리

\`\`\`javascript
[개선된 쿼리]
\`\`\`

## 인덱스 추가/변경

\`\`\`javascript
[createIndex 명령]
\`\`\`

## 실행 계획 비교

| 항목 | 튜닝 전 | 튜닝 후 |
|------|---------|---------|
| stage | COLLSCAN | IXSCAN |
| totalDocsExamined | 100,000 | 5 |
| nReturned | 5 | 5 |
| 스캔 효율 | 0.005% | 100% |
| executionTimeMillis | 850ms | 2ms |

## 예상 성능 향상

- 스캔 도큐먼트: 100,000 → 5 (20,000배 감소)
- 예상 응답 속도: 기존 대비 약 XX% 향상 *(추정 근거: executionStats 기준)*

## 적용 시 주의사항

- [인덱스 빌드 시간, 쓰기 성능 영향, 인덱스 메모리 사용량 등]
- 실제 성능은 데이터 분포와 서버 환경에 따라 다를 수 있음
```

파일 생성 후 사용자에게 저장 경로를 알린다.

---

## 주의사항
- 분석 없이 튜닝 금지. 반드시 `/mongo-query-analysis` 선행 실행.
- MongoDB의 인덱스 메모리 사용량을 고려하여 불필요한 인덱스는 추가하지 않는다.
- 복합 인덱스 설계 시 ESR 원칙(Equality → Sort → Range)을 따른다.
- 부분 인덱스는 이 프로젝트의 `isActive` Soft Delete 패턴에 적극 활용한다.
- `background: true` 옵션으로 운영 중 인덱스 생성 시 서비스 중단 방지.
