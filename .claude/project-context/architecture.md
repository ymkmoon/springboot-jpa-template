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
