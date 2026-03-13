# Agent / Workflow / Project-Context 개선 비교 리포트

> 생성일: 2026-03-13
> 비교 기준: 이전 버전 (`/review-claude` 점검 결과) → 현재 버전

---

## 1. 개선된 항목 (Before → After)

### agents/analyzer.md

| 항목 | 이전 | 현재 |
|------|------|------|
| 역할 명확화 | "analyzing code" | "analyzing code + outlining technical approach" |
| 책임 분리 | 없음 | **No Code Modification** 규칙 추가 — 구현은 backend에게만 |
| 대형 파일 처리 | 500줄 이상 grep-n만 언급 | **Java Class Surgical Read** 규칙 추가 — `grep -n`으로 메서드 탐지 후 `sed -n`으로 블록만 추출 |
| 사용자 개입 | 없음 | `STOP & ASK USER` 안전 밸브 추가 |
| 출력 형식 | Regression Test Targets, Audit Log Status | **Build Validation Targets** (Gradle 명령 포함), Audit Log 제거 |
| Audit Logging | 6th Step에 CSV 기록 필수 | **제거** — 오버헤드 감소 |
| project-map 경로 | `project-map.md` | `project-context/project-map.md` (전체 경로 명시) |

### agents/backend.md

| 항목 | 이전 | 현재 |
|------|------|------|
| N+1 방지 | 없음 | **JPA Safety Guard** 추가 — `fetch join` / `@EntityGraph` 명시 |
| 보안 검증 | 없음 | **Security-First API** 추가 — 신규/수정 엔드포인트 `@PreAuthorize` 확인 |
| 출력 형식 | "Clean Diff/Code changes, 3 items checklist" | **Exact Line Numbers** 명시 → review가 `sed -n`으로 정밀 검토 가능 |
| Handoff | 없음 | **Brief Summary for Next Agent** (Max 3 sentences) 추가 |
| Audit Logging | CSV 기록 필수 | **제거** |

### agents/planner.md

| 항목 | 이전 | 현재 |
|------|------|------|
| 도메인 식별 | 암묵적 | **Identify Domains (CRITICAL)**: `project-context/project-map.md` 먼저 참조 명시 |
| API 변경 인식 | 없음 | **API Change Awareness**: Controller 변경 시 `postman/BE_*.json` 을 Affected Files에 포함 + docs 에이전트 계획 |
| 출력 형식 | Affected Files, Audit Log Status | **Affected Domains** 분리, Task List에 역할별 브래킷 추가 |
| Audit Logging | CSV 기록 필수 | **제거** |

### agents/refactor.md

| 항목 | 이전 | 현재 |
|------|------|------|
| N+1 방지 | 없음 | **JPA & Performance Guard** 추가 — 쿼리 리팩토링 시 N+1 미발생 보장 |
| 계약 변경 금지 | "Public API signatures and return types" | **JSON response shapes** 추가 |
| Audit Logging (버그) | 리터럴 플레이스홀더 `ReadCount, ModCount` 기록 | **제거** — 오탈자 버그 소멸 |
| 출력 형식 | Audit Log Status | **Exact Line Numbers**, **Brief Summary for Next Agent** |

### agents/review.md ⭐ 가장 큰 개선

| 항목 | 이전 | 현재 |
|------|------|------|
| Anti-Loop | ai-rules.md에만 존재 | **3rd Step (Anti-Loop - CRITICAL)**: 동일 오류 2회 반복 시 STOP & ASK USER — review 내부에 직접 명시 |
| 역할 명확화 | 없음 | **4th Step (No Code Generation)**: 검증만 수행, 코드 작성/제안 금지 |
| 정밀 리뷰 | 없음 | **Surgical Review**: 이전 에이전트가 제공한 Exact Line Numbers 기반으로 `sed -n` 사용 |
| Map Validation (데드락) | docs 결과를 review 단계에서 검증 (docs는 review 이후 실행 → 구조적 데드락) | **수정**: 구조적 변경 발생 시 docs에게 업데이트 지시 (검증이 아닌 위임) |
| Audit Logging | CSV 기록 필수 | **제거** |
| 출력 형식 | Audit Log Status | Review Summary가 **Brief Summary for Next Agent** 겸용 |

### agents/docs.md

| 항목 | 이전 | 현재 |
|------|------|------|
| 책임 범위 | API 명세, README.md | **Postman collection JSON (`postman/BE_*.json`)** 추가 |
| Postman 처리 | 없음 | **4단계 절차** 추가: ls 1회 → grep/jq 탐색 → jq 업데이트(sed 금지) → jq 문법 검증 |
| 투기성 작업 | "Draft Update for technical blog" | **제거** — 투기성 작업 소멸 |
| Audit Logging | CSV 기록 필수 | **제거** |
| 출력 형식 | Audit Log Status | **Workflow Status: COMPLETE** (워크플로우 종료 신호) |

---

### workflows/feature.md

| 항목 | 이전 | 현재 |
|------|------|------|
| 각 단계 설명 | 1줄 요약 | 에이전트별 상세 지시 (Exact Line Numbers, sed-n, Postman jq) |
| docs 단계 | 3가지 조건부 실행 | **Postman JSON 갱신 조건 구체화** (jq NEVER append 명시) |
| Handoff Rule | "max 3 sentences" | **Context Pruning** 명칭 + 목적(token waste 방지) 명시 |

### workflows/bugfix.md

| 항목 | 이전 | 현재 |
|------|------|------|
| docs 단계 | 없음 | **Step 4 추가**: API contract 변경 시 Postman sync 조건부 실행 |
| 로그 처리 | 없음 | `grep -A 20 "Exception"` 팁 추가 |
| Handoff Rule | 기본 | "이전 컨텍스트 반복 금지" 명시 |

### workflows/refactoring.md

| 항목 | 이전 | 현재 |
|------|------|------|
| review 지침 | "exact error logs" | "NEVER read (cat) the entire file" + Max 20 lines |
| docs 단계 | "Update documentation only if public APIs or architecture changed" | **Postman 예외 처리 추가**: 리팩토링 중 예상치 못한 API 변경 시 jq 사용 |

### project-context/project-map.md

| 항목 | 이전 | 현재 |
|------|------|------|
| 구조 | 단순 평문 | **6개 섹션** 헤더 도입 |
| Self-Update Rule | 없음 | **Priority Zero**: docs 에이전트가 최우선으로 이 파일 갱신 |
| 공유 컨텍스트 | `exception/`, `constants/`, `config/` | `security/`, `filter/`, `aop/` 추가 |
| Postman 섹션 | 없음 | **Section 5 추가**: 디렉토리, 네이밍 컨벤션, jq Sync 규칙 |
| RepositoryCustom | 없음 | `{Domain}RepositoryCustom.java` 명시 |

### project-context/ai-rules.md

| 항목 | 이전 | 현재 |
|------|------|------|
| 구조 | 단순 목록 | **2개 섹션** (Rule Priority / Efficiency & Safety Rules) |
| 환각 방지 | 없음 | **No Hallucination (Strict Prohibition)**: 존재하지 않는 라이브러리/경로/변수명 금지, 확인 방법 명시 |
| 파일 읽기 | 없음 | **Surgical Reading (sed -n)**: 전체 cat 금지, sed -n으로 블록만 읽기 |
| 출력 스타일 | 없음 | **Zero Conversational Filler**: 인사말/filler 금지, CLI 도구처럼 동작 |
| Surgical Updates | "NEVER rewrite entire files" | **Exact Line Numbers** 전략으로 구체화 |

### project-context/tech-stack.md

| 항목 | 이전 | 현재 |
|------|------|------|
| Redis 설명 | "Redis / Embedded Redis (mac profile)" (mac=Embedded로 오해 가능) | "Redis (mac profile) / Embedded Redis" (순서 변경, 약간 개선) |

---

## 2. 검증 결과 — 잔존 오류 및 이슈

### 🔴 Critical

없음 (이전 Critical 2건 모두 해소)

---

### 🟡 Warning

#### W-1. Bash 전용 명령 vs Claude Code 툴 충돌 (미해소)

아래 파일들에 `sed -n`, `find`, `head`, `tail` 명령이 여전히 남아있다.
Claude Code 서브에이전트 환경에서 이 명령들은 Bash 툴을 강제 호출하며 Read(Glob/Grep) 툴과 충돌한다.

| 파일 | 충돌 명령 | 대체 툴 |
|------|-----------|---------|
| `analyzer.md` Step 2 | `find` | Glob |
| `analyzer.md` Step 4 | `sed -n`, `head/tail` | Read (offset/limit) |
| `review.md` Surgical Review | `sed -n '{start},{end}p'` | Read (offset/limit) |
| `bugfix.md` Step 3 | `sed -n '{start},{end}p'` | Read (offset/limit) |
| `refactoring.md` Step 3 | `sed -n '{start},{end}p'` | Read (offset/limit) |
| `ai-rules.md` Surgical Reading | `sed -n '{start},{end}p'` | Read (offset/limit) |

#### W-2. project-map.md — 실제 도메인 목록 미기재 (미해소)

구조는 크게 개선됐으나 Section 3 Feature Mapping에 실제 구현된 도메인이 없다.
`analyzer`가 project-map.md를 참조해도 `menu` 도메인을 찾을 수 없어 전체 스캔으로 fallback할 위험이 있다.

#### W-3. tech-stack.md — Redis 프로파일 설명 불명확 (부분 개선, 미해소)

- 현재: `Redis (mac profile) / Embedded Redis` — 어느 프로파일이 Embedded인지 불명확
- 정확한 내용 (CLAUDE.md 기준):
  - `mac` → Docker Redis 6380
  - `local` → Embedded Redis 6380
  - `dev` → Docker Redis 6380

---

### 🔵 Minor

#### M-1. analyzer.md — `grep -n` 후 `sed -n` 이중 지시

Step 5에서 "grep -n으로 관련 라인 번호 찾은 후 specific blocks 읽기"는 올바른 방향이나,
Step 4에서 이미 `sed -n`을 지시하고 있어 순서가 중복된다.
Grep → Read(offset/limit) 단일 패턴으로 통일하는 것이 더 명확하다.

#### M-2. project-map.md — Section 번호 누락

Section 1~6 중 Section 4가 "Shared Context Mapping", Section 5가 "API Documentation (Postman)"이나
Section 7이 없는데 Section 번호가 `## 6. Search Strategy`로 끝나 있어 실제로는 6개 섹션이다.
"## 6. Search Strategy"의 내용에 7번 항목처럼 보이는 번호 목록(1~3)이 있어 섹션 번호와 내부 순서가 혼재된다.

---

## 3. 수정 필요 항목 리스트

### [수정-1] `sed -n` → Read 툴 대체 (전체 6개 파일)

**대상 파일:**
- `.claude/agents/analyzer.md` — Step 2 (`find` → Glob), Step 4 (`sed -n`, `head/tail` → Read with offset/limit)
- `.claude/agents/review.md` — Surgical Review 섹션
- `.claude/workflows/bugfix.md` — Step 3
- `.claude/workflows/refactoring.md` — Step 3
- `.claude/project-context/ai-rules.md` — Surgical Reading 섹션

**수정 방향:**
```
Before: Use `sed -n '{start},{end}p' {file}` to read only the affected context.
After:  Use the Read tool with offset and limit parameters to read only the affected lines.

Before: Use `find` or `ls` to locate paths.
After:  Use the Glob tool to locate paths within the identified domain.
```

---

### [수정-2] `project-map.md` — 실제 도메인 목록 추가

**대상 파일:** `.claude/project-context/project-map.md`

Section 3 하단 또는 별도 Section에 현재 구현된 도메인을 기재해야 한다.

**추가할 내용 (예시):**
```markdown
## 7. Implemented Domains (Current HEAD)

| Domain | Package Path | Controller | Service | Repository |
|--------|-------------|------------|---------|------------|
| menu | `src/.../menu/` | MenuController | MenuService/Impl | MenuRepository/RepositoryCustom |
| admin | `src/.../admin/` | AdminController | AdminService/Impl | AdminRepository |
| (추가 도메인은 docs 에이전트가 신규 기능 추가 시 갱신) |
```

---

### [수정-3] `tech-stack.md` — Redis 프로파일 설명 정정

**대상 파일:** `.claude/project-context/tech-stack.md`

**수정 방향:**
```
Before: Redis (mac profile) / Embedded Redis

After:
- Docker Redis 6380 (mac profile, dev profile)
- Embedded Redis 6380 (local profile only)
```

---

### [수정-4] `analyzer.md` — Step 4/5 중복 정리

**대상 파일:** `.claude/agents/analyzer.md`

Step 4와 Step 5가 동일한 "특정 블록만 읽기" 목적으로 중복된다.
Step 4를 Read 툴(offset/limit)로 통일하면 Step 5는 별도 항목 없이 흡수된다.

---

## 4. 종합 평가

| 구분 | 이전 | 현재 |
|------|------|------|
| Critical 이슈 | 2건 | **0건** |
| Warning 이슈 | 5건 | **3건** |
| Minor 이슈 | 1건 | **2건** |
| 전체 완성도 | 약 60% | **약 80%** |

**주요 성과:**
- Audit Logging 전면 제거 → 토큰 오버헤드 감소
- review.md Anti-Loop 내재화 → 무한 루프 방지
- review.md 데드락 구조 해소 → docs 실행 전 docs 검증 문제 소멸
- Postman 동기화 전략 일관화 (jq + 검증)
- 에이전트 역할 경계 명확화 (No Code in analyzer/review)
- N+1, 보안 가드 추가 (backend, refactor)

**남은 과제:**
Bash 전용 명령(`sed -n`, `find`)을 Claude Code 네이티브 툴(Read, Glob)로 교체하는 것이 가장 우선순위가 높다.
현재 상태로도 동작하지만, Bash 툴 의존은 Claude Code 툴 승인 프롬프트를 증가시키고 일관성을 떨어뜨린다.
