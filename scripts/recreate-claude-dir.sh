#!/usr/bin/env bash
# Run from project root. Recreates .claude/ with current agents, workflows, project-context.

set -e
REPO_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]:-$0}")/.." && pwd)"
cd "$REPO_ROOT"

mkdir -p .claude/agents .claude/workflows .claude/project-context

# --- agents ---
cat > .claude/agents/analyzer.md << 'ENDFILE'
You are a senior architect analyzing code.

Goal: Identify the exact files required for the task.

Responsibilities
- Trace request flow from entry points.
- Identify dependencies and side effects.

Cost Optimization Rules
- Use search tools first (grep, ls).
- Never scan the whole repository.
- Read only files directly related to the request.

Output format

Target Files
Logic Summary
Side Effects
ENDFILE

cat > .claude/agents/backend.md << 'ENDFILE'
You are a senior backend engineer.

Goal: Implement production-safe code.

Responsibilities
- Implement logic assigned by planner.
- Follow architecture.md and error-handling.md.

Rules
- Modify only necessary files.
- Prefer extending existing modules over creating new ones.
- Always handle errors explicitly.

Output
- Code changes
- Short implementation checklist
ENDFILE

cat > .claude/agents/docs.md << 'ENDFILE'
You are a documentation specialist.

Goal: Keep documentation synchronized with code.

Responsibilities
- Update README
- Update API documentation
- Document architecture changes

Rules
- Be concise.
- Provide examples when useful.
ENDFILE

cat > .claude/agents/planner.md << 'ENDFILE'
You are a senior software planner.

Goal: Create the smallest safe execution plan.

Responsibilities
- Understand the user request.
- Break work into small tasks.
- Assign affected files when possible.

Rules
- Prefer incremental changes.
- Avoid speculative architecture changes.
- Output concise task lists only.

Output format

Context
Affected Files
Task List
ENDFILE

cat > .claude/agents/review.md << 'ENDFILE'
You are a strict senior code reviewer.

Goal: Ensure system stability.

Responsibilities
- Validate architecture compliance.
- Detect performance risks.
- Detect missing error handling.

Rules
- PASS if safe.
- FAIL if architecture or error rules are violated.
- Do not suggest purely stylistic changes.

Output

Review Summary
Issues
Status: PASS or FAIL
ENDFILE

# --- workflows ---
cat > .claude/workflows/bugfix.md << 'ENDFILE'
Workflow: Bug Fix

analyzer
→ backend
→ review
ENDFILE

cat > .claude/workflows/feature.md << 'ENDFILE'
Workflow: Feature Development

planner
→ analyzer
→ backend
→ review
→ backend (fix if FAIL)
→ docs
ENDFILE

# --- project-context ---
cat > .claude/project-context/ai-rules.md << 'ENDFILE'
AI Development Rules

Rule Priority

1 architecture.md
2 error-handling.md
3 repository-structure.md
4 api-style.md
5 coding-rules.md

Efficiency Rules

- Never read unrelated files.
- Avoid speculative refactoring.
- Keep outputs concise.
ENDFILE

cat > .claude/project-context/api-style.md << 'ENDFILE'
API Style

- Context path: `/template`.
- Resource-oriented naming (e.g. `/template/auth/sign-in`, `/template/admins/{id}`).

Response Format

`ApiResponse<T>`: `timestamp`, `code`, `message`, `data`.

{
  "timestamp": "...",
  "code": "20000000",
  "message": "성공",
  "data": {}
}

Rules

- Do not break existing API contracts.
ENDFILE

cat > .claude/project-context/architecture.md << 'ENDFILE'
Architecture: Spring Boot Layered (Controller → Service → Repository)

Layer Flow

HTTP Request
  → JwtRequestFilter (JWT validation)
  → SecurityConfig (access control)
  → Controller
  → ServiceImpl (@Transactional)
  → Repository (JPA / QueryDSL)
  → RoutingDataSource (master write / slave read)

Conventions

- Entities: `*Entity`, extend `BaseEntity` (createdAt, updatedAt, isActive, etc.).
- DTOs: inner static classes in `*Dto` (e.g. `AdminDto.AdminResponse`).
- Service: interface `*Service` + implementation `*ServiceImpl`.
- Repository: JPA `*Repository` + optional `*RepositoryCustom` (QueryDSL). Prefer V3 (QueryDSL) for new queries.

Rules

- Business logic belongs in usecase.
- Domain must remain framework independent.
- Repositories only handle database operations.
- Delivery must remain thin.
ENDFILE

cat > .claude/project-context/coding-rules.md << 'ENDFILE'
Coding Rules

- Prefer readability.
- Avoid clever tricks.
- Keep functions small.
- Follow repository structure.
ENDFILE

cat > .claude/project-context/error-handling.md << 'ENDFILE'
Error Handling

- Never ignore errors.
- Wrap errors with context.

Example

All exceptions are handled centrally by `GlobalExceptionHandler`. For business errors, throw `BusinessException` with a `ResponseCode`. Response envelope is `ApiResponse<T>` (timestamp, code, message, data).

throw new BusinessException(ResponseCode.ADMIN_NOT_FOUND);

- Define domain errors when needed.
ENDFILE

cat > .claude/project-context/project-map.md << 'ENDFILE'
Project Map

Example structure mapping

Auth Feature

auth/AuthController.java
auth/AuthService.java, auth/AuthServiceImpl.java
security/TokenProvider.java
filter/JwtRequestFilter.java

Admin Feature

admin/AdminController.java
admin/AdminService.java, admin/AdminServiceImpl.java
admin/AdminRepository.java, admin/AdminRepositoryCustom.java
common/dto/AdminDto.java

Shared / Config

config/SecurityConfig.java
config/DataSourceConfig.java
exception/GlobalExceptionHandler.java
common/ApiResponse.java
model/BaseEntity.java

Purpose

- Help AI find relevant files quickly.
- Reduce repository scanning.
ENDFILE

cat > .claude/project-context/repository-structure.md << 'ENDFILE'
Repository Structure

Base package: `com.example.template`

src/main/java/com/example/template/
  Application.java
  auth/           # sign-in, sign-out, refresh-token
  admin/          # admin CRUD, list (Controller, Service, Repository)
  config/         # Security, DataSource, QueryDSL, JWT, Redis
  security/       # TokenProvider, SecurityConstants, CustomAuthenticationProvider
  filter/         # JwtRequestFilter
  model/          # BaseEntity, entities, RoutingDataSource
  exception/      # GlobalExceptionHandler, BusinessException, JWT entry/denied handlers
  redis/          # RedisService
  refresh/        # RefreshToken repository
  common/         # ApiResponse, DTOs, paging
  constants/      # ResponseCode, ApprovalStatus, AuthConstants
  error/          # FailResponse, CustomErrorController
  aop/            # LogAspect
  util/

src/main/resources/
  application.yml, application-{profile}.yml
  data.sql

src/main/generated/   # QueryDSL Q-classes (do not edit)

src/test/
ENDFILE

cat > .claude/project-context/tech-stack.md << 'ENDFILE'
Tech Stack

Backend
Java 17
Spring Boot 3.2.x
Spring Security
Spring Data JPA
QueryDSL 5.x
JJWT (JWT)
Gradle

Database
MySQL 8 (dev/prod)
H2 in-memory, MODE=MySQL (mac/local)

Cache / Session
Redis (access token store)
Embedded Redis (local profile)

Infra / Observability
Spring Actuator
Prometheus
Docker (Redis, optional)

Dev Environment
macOS Apple Silicon (default profile: mac)
ENDFILE

cat > .claude/project-context/testing-strategy.md << 'ENDFILE'
Testing Strategy

Focus Areas

- business logic
- usecases
- pure functions

Service layer (e.g. `*ServiceImpl`) and repository layer (JPA / QueryDSL) are primary targets. Use `@Transactional(readOnly = true)` for read-only tests where applicable.

Pattern

Arrange
Act
Assert
ENDFILE

# --- settings (optional; often gitignored) ---
cat > .claude/settings.local.json << 'ENDFILE'
{
  "permissions": {
    "allow": [
      "Bash(./gradlew compileJava 2>&1)",
      "Bash(./gradlew clean compileJava 2>&1)"
    ]
  }
}
ENDFILE

echo "Done. .claude/ recreated (agents, workflows, project-context, settings.local.json)."
