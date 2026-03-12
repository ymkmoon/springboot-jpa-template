Tech Stack & Compatibility Rules

Backend
- Java 17 (Switch expressions, Sealed classes)
- Spring Boot 3.2.x
- Spring Security, JPA, QueryDSL 5.x, Gradle
- JJWT (Use latest secure patterns)

Anti-Patterns (DO NOT USE)
- No `System.out.println` (Use `@Slf4j`)
- No `@Data` on JPA Entities (Use `@Getter`, `@Setter` separately to avoid recursion)
- No field injection (`@Autowired`)
- No manual SQL strings in Service layer

Dev & Database
- MySQL 8 / H2 (MySQL Mode)
- Redis (mac profile) / Embedded Redis 

Search Tip: If a library version is unclear, check `build.gradle` before adding new dependencies.
