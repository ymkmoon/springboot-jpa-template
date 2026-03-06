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
