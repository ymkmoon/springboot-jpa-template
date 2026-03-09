Tech Stack

Backend
Java 17
Spring Boot 3.2.x
Spring Security
Spring Data JPA
QueryDSL 5.x
JJWT (JWT)
Gradle

Database
MySQL 8 (dev/prod)
H2 in-memory, MODE=MySQL (mac/local)

Cache / Session
Redis (access token store)
Embedded Redis (local profile)

Infra / Observability
Spring Actuator
Prometheus
Docker (Redis, optional)

Testing
JUnit 5 (JUnit Jupiter)
Mockito (via spring-boot-starter-test)
Spring Boot Test / MockMvc (@WebMvcTest, @ExtendWith)
spring-security-test (SecurityMockMvcRequestPostProcessors)

Dev Environment
macOS Apple Silicon (default profile: mac)
