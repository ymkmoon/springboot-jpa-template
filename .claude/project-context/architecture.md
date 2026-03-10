Architecture: Spring Boot Layered (Controller → Service → Repository)

Core Flow
1. Delivery: Controller handles HTTP mapping and DTO conversion.
2. Usecase: ServiceImpl handles @Transactional business logic and domain rules.
3. Repository: Data access via JPA or QueryDSL (Preferred).

Architectural Boundaries (Rules)
- **Dependency Flow**: Controller → Service → Repository. Never skip a layer.
- **Data Isolation**: Never expose Entities to the Delivery layer. Use DTOs for all API inputs/outputs.
- **Persistence**: All DB-related logic stays in the Repository. Use `QueryDSL` for complex joins.
- **Abstraction**: Always separate Service (Interface) and ServiceImpl (Implementation).

Reference Strategy
- Do not assume infrastructure details (Security/DataSource). If a task involves infra, use `analyzer` to read `config/` or `security/` packages directly.
