# Coding Rules

## 1. Core Principles
- **Readability Over Brevity**: Prioritize readability and maintainability over concise but cryptic code.
- **No Clever Tricks**: Avoid complex logic or one-liner tricks that are difficult to understand.
- **Small Functions**: Each method must perform a single task and should ideally be kept under 20 lines.
- **Strict Structure**: Strictly adhere to the predefined package structure and layered architecture.

## 2. Dependency Injection (DI) & Lombok
- **Constructor Injection Only**: All dependency injections must use the constructor injection pattern.
- **RequiredArgsConstructor**: Use Lombok's `@RequiredArgsConstructor` to automatically generate constructors.
- **Final Fields**: All injected fields must be declared as `private final` to ensure immutability.
- **NO @Autowired**: Field injection using `@Autowired` is strictly prohibited to prevent agent regression and ensure testability.

## 3. Database & Querying
- **QueryDSL (V3) First**: Prioritize QueryDSL (V3) for new retrieval logic over Native SQL (V1) or JPQL (V2).
- **JPA Conventions**: 
  - All entities must extend `BaseEntity` to manage common fields (e.g., `createdAt`, `updatedAt`).
  - Use the `*Entity` suffix for entity class names.
- **Soft Delete**: Implement logical deletion via `isActive = false` instead of physical deletion.
- **Schema Alignment (CRITICAL)**: Any change to Entity fields must be synchronized with database schema scripts (e.g., Flyway, `data.sql`).

## 4. Layered Architecture Patterns
- **Service Layer**: Separate interfaces (`*Service`) and implementations (`*ServiceImpl`).
- **DTO Usage**: 
  - Never expose entities directly to external APIs.
  - Define DTOs as static inner classes within a `*Dto` class for better management.
- **ReadOnly Transactions**: Explicitly use `@Transactional(readOnly = true)` for read-only service methods to improve performance and readability.

## 5. Error Handling & Response
- **BusinessException**: Throw `BusinessException` with a defined `ResponseCode` for business logic errors.
- **Global Handler**: All exceptions must be centrally managed by the `GlobalExceptionHandler`.
- **Response Envelope**: Wrap all API responses in the common standard format, `ApiResponse<T>`.

## 6. Logging & Style
- **Slf4j**: Use `@Slf4j` for logging instead of `System.out.println`.
- **Early Return**: Encourage the Early Return pattern to avoid deeply nested if-statements.
- **Optional**: Use `Optional<T>` explicitly for return types that may contain null values.

## 7. Comments & Documentation
- **Self-Documenting Code**: Explain intent through clear field and method names. Use comments only to explain "Why," not "What."
- **Log Levels**: 
  - `INFO`: Business process start/end.
  - `DEBUG`: Parameter information and query conditions.
  - `ERROR`: Exceptions and system errors (Include truncated stack traces).
- **No Cleanup Comments**: Do not leave unnecessary comments like `TODO` or `FIXME` in the production code.

## 8. Performance & Stability Awareness
- **Pagination**: List retrieval APIs must always use `Pageable` or return a restricted number of items.
- **N+1 Problem**: Proactively prevent the N+1 problem by using Fetch Joins or Entity Graphs for associated entity retrieval.
- **Test-Driven Stability**: Logic changes or refactoring are prohibited without corresponding test coverage. Follow the Arrange-Act-Assert (AAA) pattern.
