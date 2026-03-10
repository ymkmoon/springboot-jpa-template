Repository Structure

Base package: `com.example.template`

Core Infrastructure & Entry Points
src/main/java/com/example/template/
  Application.java
  config/         # Configuration (Security, DataSource, QueryDSL, JWT, Redis)
  security/       # Security mechanisms (Providers, Constants)
  filter/         # HTTP Filters (JwtRequestFilter)
  model/          # Shared Data Models (BaseEntity, RoutingDataSource)
  exception/      # Global Exceptions & Handlers
  redis/          # Redis integrations
  refresh/        # RefreshToken management
  common/         # Shared resources (ApiResponse, DTO interfaces, paging)
  constants/      # Global Constants (ResponseCode, ApprovalStatus)
  error/          # Custom HTTP Error controllers
  aop/            # Aspect-Oriented Programming (Logging)
  util/           # Utility classes

Dynamic Feature Domains (See project-map.md)
Business domains (e.g., auth, admin, etc.) are located directly under `src/main/java/com/example/template/{domain}/` and follow their own internal layered structure.

Resources & Generators
src/main/resources/
  application.yml, application-{profile}.yml
  data.sql

src/main/generated/   # QueryDSL Q-classes (READ-ONLY, do not edit)
