# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

---

# Agent & Workflow Rules (CRITICAL)

For complex tasks (new features, bug fixes, refactoring), ALWAYS follow these steps to minimize token usage and ensure zero-regression:

1. **Read project context**
   - Load only necessary files from `.claude/project-context/` (architecture.md, coding-rules.md, etc.).

2. **Search Before Reading**
   - Use `grep`, `find`, or `ls` to identify logic.
   - NEVER `cat` entire files just to find a method.

3. **Select the correct workflow** from `.claude/workflows/`

- New feature → `feature.md`
  planner → analyzer → backend → review → docs

- Bug fix → `bugfix.md`
  analyzer → backend → review

- Refactoring → `refactoring.md`
  analyzer → refactor → review

4. **Efficiency & Safety Rules**

- Surgical Updates ONLY (no full file rewrites)
- Log truncation (max 20 lines)
- No Test → No Refactor

Simple single-file changes may skip workflow.

---

# Build & Run

Build

./gradlew clean build

Run

./gradlew bootRun --args='--spring.profiles.active=mac'
./gradlew bootRun --args='--spring.profiles.active=local'
./gradlew bootRun --args='--spring.profiles.active=dev'

Run targeted tests

./gradlew test --tests "com.example.template.domain.{domain}.*"

Generate QueryDSL classes

./gradlew compileJava

---

# Profiles

| Profile | DB | Redis | Port |
|-------|------|------|------|
| mac | H2 in-memory | Docker 6380 | 8000 |
| local | H2 in-memory | Embedded 6380 | 8000 |
| dev | MySQL | Docker 6380 | 8001 |

---

# Architecture & Constraints

Request Flow

JwtRequestFilter → SecurityConfig → Controller → ServiceImpl → Repository → RoutingDataSource

---

# Database Stability

Schema Sync (CRITICAL)

When modifying `*Entity`, check:

src/main/resources/data.sql

Ensure DB schema matches the entity.

Soft Delete

Always use:

isActive

Do NOT use hard DELETE queries.

QueryDSL

Use **V3 QueryDSL** for new features unless V1 or V2 is required.

---

# Entity & DTO Conventions

Entities

*Entity → extend `BaseEntity`

DTO

Inner static classes inside `*Dto`

Example

AdminDto.AdminResponse

Service

Interface

*Service

Implementation

*ServiceImpl

---

# Exception Handling

All exceptions go through:

GlobalExceptionHandler

Throw:

BusinessException

Response wrapper:

ApiResponse<T>

---

# Key Paths

| Concern | Path |
|------|------|
| Security config | config/SecurityConfig.java |
| Datasource routing | config/DataSourceConfig.java |
| Global exception handler | exception/GlobalExceptionHandler.java |
| Base entity | model/BaseEntity.java |
| Initial seed data | src/main/resources/data.sql |

---

# Important Notes

Targeted Test Execution

DO NOT run the entire test suite for routine checks.

Always run targeted tests for impacted domain.

Anti-Loop

If review fails twice → STOP and ask the user.

No Speculation

Do not perform speculative refactoring.

Only implement what is explicitly requested.

