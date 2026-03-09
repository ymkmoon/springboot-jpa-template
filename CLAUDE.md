# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Agent & Workflow Rules

For complex tasks (new features, bug fixes, refactoring), ALWAYS follow these steps before writing any code:

1. **Read project context** — load relevant files from `.claude/project-context/` (architecture.md, coding-rules.md, error-handling.md, etc.)
2. **Select the correct workflow** from `.claude/workflows/`:
   - New feature → `feature.md`: planner → analyzer → backend → review → backend (if FAIL) → docs
   - Bug fix → `bugfix.md`: analyzer → backend → review
   - Refactoring → refactoring.md: analyzer → refactor → review → refactor (if FAIL) → docs
3. **Use subagents** defined in `.claude/agents/` for each step:
   - `planner.md` — break work into tasks, identify affected files
   - `analyzer.md` — trace request flow, identify dependencies
   - `backend.md` — implement production-safe code
   - `review.md` — validate architecture compliance (PASS/FAIL)
   - `docs.md` — update documentation
   - `refactor.md` — improve code quality without changing external behavior

Simple, single-file changes do not require the full workflow.

## Build & Run

```bash
# Build
./gradlew clean build           # Mac/Linux
gradlew clean build             # Windows

# Run with profile
./gradlew bootRun --args='--spring.profiles.active=mac'    # Mac
./gradlew bootRun --args='--spring.profiles.active=local'  # Windows (embedded Redis)
./gradlew bootRun --args='--spring.profiles.active=dev'    # Dev (MySQL)

# Run tests
./gradlew test

# QueryDSL Q-class generation (runs automatically on build)
./gradlew compileJava
```

## Profiles

| Profile | DB | Redis | Port |
|---------|-----|-------|------|
| `mac` | H2 in-memory | Docker on 6380 | 8000 |
| `local` | H2 in-memory | Embedded on 6380 | 8000 |
| `dev` | MySQL | Docker on 6380 | 8001 |

Mac Docker Redis setup:
```bash
docker run --name redis-local -p 6380:6379 -d redis:latest
```

H2 Console (mac/local profiles): `http://localhost:8000/template/h2-console`
- JDBC URL: `jdbc:h2:mem:ymk` | User: `sa` | Password: (empty)

## Architecture

### Request Flow

```
HTTP Request
  → JwtRequestFilter (JWT validation)
  → SecurityConfig (access control)
  → Controller
  → ServiceImpl (@Transactional)
  → Repository (JPA / QueryDSL)
  → RoutingDataSource (write vs read)
```

### Multi-Datasource Routing

`DataSourceConfig` sets up master/slave routing via `AbstractRoutingDataSource`. The routing key is determined by transaction type: `@Transactional(readOnly = true)` routes to the read datasource; write transactions go to the master. `LazyConnectionDataSourceProxy` defers connection acquisition until actually needed.

### JWT Authentication Flow

1. `POST /template/auth/sign-in` → `CustomAuthenticationProvider` validates credentials
2. `TokenProvider` issues AccessToken (10 min in config, 30 min per README) + RefreshToken (3 days)
3. Tokens stored in Redis keyed by admin UUID
4. `JwtRequestFilter` validates every request before it reaches controllers
5. `POST /template/auth/refresh-token` exchanges a valid RefreshToken for new tokens

### Security Whitelist

Defined in `SecurityConstants`. No auth required for:
- `/auth/sign-in`, `/auth/sign-up`
- `/h2-console/**`, `/actuator/health`, `/actuator/prometheus`

### Repository Query Versions

The admin list API has three parallel implementations in `AdminRepository`:
- **V1** – Native SQL (`@Query(nativeQuery = true)`)
- **V2** – JPQL (`@Query`)
- **V3** – QueryDSL (via `QuerydslConfig`-provided `JPAQueryFactory`)

This pattern exists to compare query approaches. New features should use V3 (QueryDSL) unless there is a reason to use the others.

### Entity & DTO Conventions

- Entities: `*Entity`, extend `BaseEntity` (has `createdAt`, `updatedAt`, `createdBy`, `updatedBy`, `isActive` for soft delete)
- DTOs: inner static classes inside a `*Dto` class (e.g., `AdminDto.AdminResponse`)
- Service: interface `*Service` + implementation `*ServiceImpl`

### Account Status

`AdminEntity.approvalStatus` must be `ACTIVE` and `isActive = true` for login. Status values: `ACTIVE`, `PENDING`, `REJECTED`, `SUSPENDED`, `WITHDRAWN`.

### Exception Handling

All exceptions funnel through `GlobalExceptionHandler`. Throw `BusinessException` for business logic errors. The response envelope is:
```json
{ "timestamp": "...", "code": "20000000", "message": "성공", "data": {} }
```

## Key Paths

| Concern | Path |
|---------|------|
| Security config | `config/SecurityConfig.java` |
| Datasource routing | `config/DataSourceConfig.java` |
| JWT logic | `security/TokenProvider.java` |
| Auth filter | `filter/JwtRequestFilter.java` |
| Global exception handler | `exception/GlobalExceptionHandler.java` |
| Base entity | `model/BaseEntity.java` |
| Initial seed data | `src/main/resources/data.sql` |
| QueryDSL generated classes | `src/main/generated/` (gitignored) |

## Important Notes

- The active profile defaults to `mac` in `application.yml`. Change before running on Windows.
- H2 runs in `MODE=MySQL` for local profiles — be cautious with MySQL-specific SQL syntax that H2 may not support.
- `spring.jpa.hibernate.ddl-auto=create-drop` in non-dev profiles; `dev` uses `none` (manual schema management).
- JWT secret keys in config files are for local/dev only and must be externalized for any production use.
- QueryDSL Q-classes are generated into `src/main/generated/` — do not edit these files manually.
- AOP logging (`LogAspect`) logs all service method calls including parameters; avoid logging sensitive data in service methods.
