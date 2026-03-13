# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

---

# Agent & Workflow Rules (CRITICAL)

For complex tasks (new features, bug fixes, refactoring), ALWAYS follow these steps to minimize token usage and ensure zero-regression:

1. **Read project context**
   - Load only necessary files from `.claude/project-context/` (`project-map.md`, `architecture.md`, etc.).
   - ALWAYS consult `project-map.md` BEFORE searching for files.

2. **Search Before Reading**
   - Use `grep`, `find`, or `ls` to identify logic.
   - NEVER `cat` entire files just to find a method.

3. **Select the correct workflow** from `.claude/workflows/`
   - New feature → `feature.md`
     (`planner` → `analyzer` → `backend` → `review` → `docs`)
   - Bug fix → `bugfix.md`
     (`analyzer` → `backend` → `review` → `docs` if API changes)
   - Refactoring → `refactoring.md`
     (`analyzer` → `refactor` → `review` → `docs` if architecture changes)

4. **Efficiency & Safety Rules (Global)**
   - **Surgical Updates ONLY**: No full file rewrites.
   - **Precision Handoff**: Agents MUST pass "Exact Line Numbers" to the next agent.
   - **Context Pruning**: Hand-offs must include a max 3-sentence summary.
   - **File Reading limits**: Use `sed -n` to read specific blocks. Avoid `cat`.
   - **JSON Safety**: ALWAYS use `jq` to update `postman/BE_*.json`. NEVER use `sed` or `append`.
   - **No Test → No Refactor**.
   - **Zero Conversational Filler**: Act like a CLI tool. Omit greetings or conversational filler. Output ONLY logs, diffs, or required summaries.

*Simple single-file changes may skip the formal workflow but MUST adhere to Efficiency Rules.*

---

# Build & Run

Build:
`./gradlew clean build`

Run:
`./gradlew bootRun --args='--spring.profiles.active=mac'`
`./gradlew bootRun --args='--spring.profiles.active=local'`
`./gradlew bootRun --args='--spring.profiles.active=dev'`

Run targeted tests:
`./gradlew test --tests "com.example.template.{domain}.*"`

Generate QueryDSL classes:
`./gradlew compileJava`

---

# Profiles

| Profile | DB | Redis | Port |
|-------|------|------|------|
| mac | H2 in-memory | Docker 6380 | 8000 |
| local | H2 in-memory | Embedded 6380 | 8000 |
| dev | MySQL | Docker 6380 | 8001 |

---

# Architecture & Constraints

Request Flow:
`JwtRequestFilter` → `SecurityConfig` → `Controller` → `ServiceImpl` → `Repository` → `RoutingDataSource`

---

# Database Stability

Schema Sync (CRITICAL):
When modifying `*Entity`, check `src/main/resources/data.sql`. Ensure DB schema matches the entity.

Soft Delete:
Always use `isActive`. Do NOT use hard DELETE queries.

QueryDSL:
Use **V3 QueryDSL** for new features unless V1 or V2 is required.

---

# Entity & DTO Conventions

- **Entities**: `*Entity` → extend `BaseEntity`
- **DTO**: Inner static classes inside `*Dto` (Example: `AdminDto.AdminResponse`)
- **Service Interface**: `*Service`
- **Service Implementation**: `*ServiceImpl`

---

# Exception Handling

All exceptions go through: `GlobalExceptionHandler`
Throw: `BusinessException`
Response wrapper: `ApiResponse<T>`

---

# Key Paths

| Concern | Path |
|------|------|
| API Documentation (Postman) | `postman/BE_*.json` |
| Security config | `config/SecurityConfig.java` |
| Datasource routing | `config/DataSourceConfig.java` |
| Global exception handler | `exception/GlobalExceptionHandler.java` |
| Base entity | `model/BaseEntity.java` |
| Initial seed data | `src/main/resources/data.sql` |

---

# SQL Rules & Sub-agent Guidelines

**분석 우선:** 모든 SQL 리팩토링 및 튜닝 작업 시, 먼저 해당 DBMS의 분석 스킬을 사용하여 실행 계획을 확보할 것.

**튜닝 절차:**
1. 슬로우 쿼리나 개선 대상을 선정한다.
2. 분석 스킬을 통해 병목 지점을 파악한다.
3. 튜닝 스킬을 통해 개선된 쿼리와 인덱스 전략을 도출한다.

**결과 보고:** 튜닝 후에는 반드시 기존 쿼리 대비 예상 성능 향상 폭을 보고할 것.

| DBMS | Analysis Skill | Tuning Skill |
|------|-----------|-----------|
| MySQL | `/mysql-query-analysis` | `/mysql-query-tuning` |
| PostgreSQL | `/postgres-query-analysis` | `/postgres-query-tuning` |
| MongoDB | `/mongo-query-analysis` | `/mongo-query-tuning` |

---

# Important Notes

- **Targeted Test Execution**: DO NOT run the entire test suite for routine checks. Always run targeted tests for the impacted domain.
- **Anti-Loop**: If review fails twice → STOP and ask the user.
- **No Speculation**: Do not perform speculative refactoring. Only implement what is explicitly requested.