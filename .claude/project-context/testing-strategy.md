Testing Strategy

Focus Areas: Business logic, Usecases, Pure functions.

Unit Test Pattern: Arrange → Act → Assert

**Regression & Safety Strategy**
- **Dependency Testing**: When a Service is modified, you MUST also run the corresponding Controller tests to ensure the entry point is still functional.
- **Refactoring Guard**: Refactoring is only complete when both the unit tests and the upper-layer integration tests pass.
- **Dynamic Error Lookup**: Always check `ResponseCode.java` for exact failure codes. Do not guess.

Success & Failure Cases
- Every CRUD operation must have both success and failure (exception) test cases.
- **Targeted Test Execution (Token Saving)**: DO NOT run the entire test suite (`./gradlew test`) for routine checks, as it wastes time and tokens. ALWAYS run targeted tests using `./gradlew test --tests "com.example.template.domain.*"` for the specific files or domains impacted by the change.
