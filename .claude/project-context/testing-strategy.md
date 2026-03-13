# Testing Strategy & Data Isolation

**Goal**: Ensure logic correctness and prevent regression through isolated, repeatable, and surgically targeted test execution.

---

## 1. Core Testing Principles
- **Pattern**: Always follow the **Arrange → Act → Assert (AAA)** pattern.
- **Scope**: Focus primarily on Business Logic (Services), Usecases, and Data Mapping.
- **Mocking**: Use `@MockBean` or `Mockito` to isolate the unit under test from external infrastructure (e.g., Third-party APIs, Redis).

## 2. Data Isolation & Clean State (CRITICAL)
- **Automatic Rollback**: Every `@SpringBootTest` or `@DataJpaTest` that interacts with a database MUST be annotated with `@Transactional`. This ensures each test method runs in its own transaction and rolls back at the end.
- **Zero Dependency**: Tests must be executable in any order. Never share state between test methods (e.g., avoid static variables unless they are constants).
- **Environment**: Use an in-memory database (e.g., H2 in MySQL mode) for unit and integration tests to avoid polluting the local development database.

## 3. Regression & Surgical Validation
- **Dependency Guard**: When a Service is modified, you MUST also execute the corresponding Controller tests to ensure the API entry point remains functional.
- **Refactoring Safety**: A refactor is only considered successful when both the unit tests for the modified logic and the upper-layer integration tests pass.
- **Dynamic Lookup**: Always verify failure scenarios using exact codes from `constants/ResponseCode.java`. Do not hardcode or guess error messages.

## 4. Execution Rules (The Surgical Strike)
- **Targeted Test Execution (Token Saving)**: DO NOT run the entire test suite (`./gradlew test`). This is a waste of time and tokens. 
- **Command Standard**: ALWAYS run targeted tests using the specific domain package.
  - *Command*: `./gradlew test --tests "com.example.template.{domain}.*"`
- **API Contract Validation**: When testing Controllers (MockMvc), you MUST strictly assert the JSON response keys and types using `jsonPath`. This ensures implementation stays in sync with `postman/BE_*.json`.

## 5. Success/Failure Requirements
- Every new feature or bug fix must include:
  1. **Success Case**: Positive flow with expected data.
  2. **Failure Case**: Exception handling for invalid inputs or business rule violations.
