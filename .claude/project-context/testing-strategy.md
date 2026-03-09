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

Unit Test Strategy

Test Synchronization
- Whenever production code is added or modified, the related test code must be updated and verified in the same change.
- A feature is not considered complete until both production code and its tests pass.
- If a service method signature, return type, or exception changes, all affected test methods must be updated immediately.

Mocking
- External dependencies (Repository, RedisService, TokenProvider, etc.) must be mocked using Mockito — never use real implementations.
- Focus solely on the System Under Test (SUT): pure business logic inside `*ServiceImpl`.
- Service tests: `@ExtendWith(MockitoExtension.class)` + `@Mock` / `@InjectMocks`.
- Controller tests: `@WebMvcTest` + `@MockBean` for service + security mocks; use `SecurityMockMvcRequestPostProcessors.user()` for `@AuthenticationPrincipal` endpoints.

Read Scenario Separation
- If a query supports filtering/search conditions, implement separate test methods:
  - `*_전체조회_성공` — no conditions, returns all records.
  - `*_조건검색_성공` — with at least one filter condition (e.g. loginId, name, email).
- Each method must independently verify result size, field values, and pagination (totalCount, list) if applicable.
- Admin list has three implementations (V1/V2/V3); test each separately to cover Native SQL, JPQL, and QueryDSL paths.

CRUD Success & Failure Cases
- Every CRUD operation must have both:
  - Success case: verify expected return value, field mapping, or state change.
  - Failure (exception) case: verify correct exception type and `ResponseCode` are thrown.
- Required failure scenarios by operation:
  - Create: duplicate constraint → e.g. `ALREADY_REGIST_LOGIN_ID`, `ALREADY_REGIST_PHONE_NUMBER`
  - Read (single): not found → e.g. `AUTHORITY_GROUP_NOT_FOUND`, `USER_NAME_NOT_FOUND`
  - Update: group/level not found → `AUTHORITY_GROUP_NOT_FOUND`, `AUTHORITY_LEVEL_NOT_FOUND`
  - Delete: dependency constraint → `AUTHORITY_GROUP_HAS_ACTIVE_ADMINS`
  - Menu access: returns `accessible = false` when no mapping exists
