# Coding Rules & Implementation Standards

**Goal**: Maintain high code quality and prevent technical debt through strict architectural compliance and surgical precision.

---

## 1. Core Principles
- **Readability Over Brevity**: Prioritize maintainability. Avoid "clever" one-liners.
- **Small Functions**: Keep methods under 20 lines. One method, one task.
- **Surgical Implementation (CRITICAL)**: Only modify the lines identified by the `analyzer`. Do not perform unrelated "drive-by" refactoring.

## 2. Dependency Injection (DI) & Lombok
- **Constructor Injection**: All DI must use `@RequiredArgsConstructor`.
- **Final Fields**: Injected fields must be `private final` to ensure immutability.
- **NO @Autowired**: Field injection is strictly prohibited to ensure testability.

## 3. Database & QueryDSL (The V3 Standard)
- **QueryDSL 5.x (V3: RepositoryCustom Pattern)**: 
  - Priority: QueryDSL (V3) > JPQL (V2) > Native SQL (V1).
  - **Structure**: Always use the `RepositoryCustom` / `RepositoryImpl` pattern to keep QueryDSL logic separate from Spring Data JPA interfaces.
- **JPA Safety**: 
  - All entities must extend `BaseEntity`.
  - **N+1 Prevention**: Proactively use `fetch join` or `@EntityGraph` for associated collections.
- **Soft Delete**: Use `isActive = false` instead of physical deletion.
- **Schema Alignment**: Entity changes MUST be reflected in `src/main/resources/data.sql`.

## 4. Transaction Management (CRITICAL)
- **Service Layer Initiation**: Transactions MUST start in the Service implementation (`*ServiceImpl`). 
- **Atomicity**: Never put `@Transactional` on Controllers. Ensure the entire business use case is atomic within the Service layer.
- **Read-Only Optimization**: Use `@Transactional(readOnly = true)` for all fetch/list operations to improve performance and prevent accidental flushes.

## 5. Layered Architecture Patterns
- **Abstraction**: Separate `*Service` (Interface) and `*ServiceImpl` (Implementation).
- **DTO Isolation**:
  - NEVER expose Entities to Controllers.
  - Use static inner classes within a `*Dto` class for related request/response models.
- **Early Return**: Use the Early Return pattern to minimize nested if-statements.

## 6. Error Handling & Response
- **BusinessException**: Use `throw new BusinessException(ResponseCode.XXX);`.
- **Optional**: Use `Optional<T>` for return types that may be null. Never return null for collections (return `Collections.emptyList()`).

## 7. Performance & Stability
- **Pagination**: List APIs MUST use `Pageable` or return a slice.
- **Logging**: Use `@Slf4j`. 
  - `INFO`: Process start/end.
  - `DEBUG`: Query conditions and parameters.
  - `ERROR`: Exceptions with truncated stack traces.
