---
name: review-claude
description: Review all .claude/ agent, workflow, and project-context files against the cost-reduction and safety strategy (Surgical Strike, no full-file scan, test-driven stability). Outputs a prioritized issue report.
user-invocable: true
allowed-tools: Read, Glob, Grep, Bash
---

# /review-claude — .claude 설정 전체 점검

## 목적
`.claude/` 하위의 모든 agent, workflow, project-context 파일을 읽고,
아래 **3가지 전략 축** 기준으로 이슈를 탐지하여 우선순위별 리포트를 출력한다.

---

## 전략 축 (판단 기준)

1. **Surgical Strike** — 불필요한 전체 파일 읽기·재작성 금지, 최소 변경 원칙
2. **전체 파일 스캔 금지** — 도메인 특정 후 탐색, 재귀 스캔 금지
3. **테스트 주도 안정성** — 로직 변경 시 테스트 없이 진행 금지, 회귀 방지

---

## 실행 절차

### Step 1 — 파일 목록 수집
아래 경로의 모든 `.md` 파일을 읽는다. 누락 없이 전부 읽을 것.

```
.claude/agents/
.claude/workflows/
.claude/project-context/
```

### Step 2 — 파일별 점검 항목

**Agents (`agents/*.md`)**
- [ ] Pre-Handoff Gate 존재 여부 (코드를 수정하는 에이전트: backend, refactor)
- [ ] Bash 전용 명령(`head`, `tail`, `sed`, `find`) 사용 여부 → Claude Code 툴 충돌
- [ ] 출력 형식이 간결한지 (Max line 제한 존재 여부)
- [ ] 에이전트 간 책임 중복 여부

**Workflows (`workflows/*.md`)**
- [ ] 모든 핸드오프에 Pre-Handoff Gate 참조 또는 명시 여부
- [ ] docs 단계에 조건부 실행(Run ONLY IF) 존재 여부
- [ ] Hand-off Rule (max 3 sentences) 존재 여부
- [ ] Anti-Loop 장치 존재 여부 (review 2회 실패 → 중단)

**Project Context (`project-context/*.md`)**
- [ ] 파일 간 규칙 충돌 여부 (예: DTO 정의, 레이어 규칙)
- [ ] `project-map.md`에 실제 도메인 목록 기재 여부
- [ ] 프로젝트 실제 스택과 맞지 않는 내용 여부 (예: 없는 기술 참조)

### Step 3 — 교차 점검

- 에이전트 정의와 워크플로우 간 일관성 확인
  - 예: `backend.md`에 게이트가 있으면 `feature.md`, `bugfix.md`에도 반영됐는지
- 타이밍 충돌 확인
  - 예: review 단계에서 review 이후 실행되는 에이전트의 결과물을 검증하는 구조
- 데드락 구조 확인
  - review FAIL 시, 수정 책임이 있는 에이전트가 review **이전**에 실행되는지

### Step 4 — 리포트 출력

아래 형식으로 출력한다. 이슈가 없는 항목은 생략한다.

```
## 점검 결과

### 🔴 Critical
- [파일명] 이슈 설명 / 전략 축

### 🟡 Warning
- [파일명] 이슈 설명 / 전략 축

### 🔵 Minor
- [파일명] 이슈 설명 / 전략 축

### ✅ 이상 없음
이슈 없으면 이 항목만 출력: "전략 기준 이슈 없음"
```

---

## 주의사항
- 이슈가 없으면 "전략 기준 이슈 없음"만 출력한다. 과잉 설명 금지.
- 추측성 개선 제안은 출력하지 않는다. 명확한 위반 사항만 리포트한다.
- 수정은 하지 않는다. 리포트만 출력한다.
