# Coding Rules

## 1. Core Principles
- **Readability Over Brevity**: 코드는 간결함보다 가독성과 유지보수성을 우선합니다.
- **No Clever Tricks**: 이해하기 어려운 복잡한 기교나 한 줄짜리 트릭을 지양합니다.
- **Single Responsibility (Small Functions)**: 하나의 메서드는 하나의 일만 수행하며, 가급적 20라인 이내로 유지합니다.
- **Strict Structure**: 정해진 패키지 구조와 레이어 아키텍처(Controller-Service-Repository)를 엄격히 준수합니다.

## 2. Dependency Injection (DI) & Lombok
- **Constructor Injection Only**: 모든 의존성 주입은 생성자 주입 방식을 사용합니다.
- **RequiredArgsConstructor**: Lombok의 `@RequiredArgsConstructor`를 사용하여 생성자를 자동 생성합니다.
- **Final Fields**: 주입받는 모든 필드는 반드시 `private final`로 선언하여 불변성을 보장합니다.
- **NO @Autowired**: 필드 주입(`@Autowired`)을 엄격히 금지합니다.

## 3. Database & Querying
- **Query Strategy Priority**: QueryDSL > JPQL > Native SQL 순으로 사용을 권장합니다.
- **JPA Conventions**: 
  - 모든 엔티티는 `BaseEntity`를 상속받아 `createdAt`, `updatedAt` 등을 공통 관리합니다.
  - 엔티티 클래스명은 `*Entity` 접미사를 사용합니다.
- **Soft Delete**: 데이터 삭제 시 실제 삭제 대신 `isActive` 또는 `deletedAt` 필드를 통한 논리 삭제를 기본으로 합니다.

## 4. Layered Architecture Patterns
- **Service Interface**: 확장성과 추상화를 위해 `*Service`(Interface)와 `*ServiceImpl`(Class)을 분리합니다.
- **DTO Usage**: 
  - 엔티티는 절대 외부로 노출하지 않으며, API 입출력에는 반드시 DTO를 사용합니다.
  - DTO는 Request/Response 용도별로 `static inner class` 구조로 관리합니다.
- **Transactional Consistency**: 
  - 비즈니스 로직이 포함된 서비스 메서드에는 `@Transactional`을 필수 적용합니다.
  - 조회 전용 메서드에는 `@Transactional(readOnly = true)`를 명시합니다.

## 5. Error Handling & Response
- **Custom Exceptions**: 비즈니스 예외 발생 시 `ResponseCode`를 포함한 `BusinessException`을 발생시킵니다.
- **Global Handler**: 모든 예외는 `GlobalExceptionHandler`에서 가로채어 공통된 형식으로 응답합니다.
- **Response Envelope**: 모든 API 응답은 `ApiResponse<T>` 규격으로 래핑합니다.

## 6. Logging & Style
- **Slf4j**: 로그 기록 시 `@Slf4j`를 사용하며, 운영 환경을 고려해 로그 레벨(INFO, ERROR)을 준수합니다.
- **Early Return**: 조건 확인 시 `if-else` 중첩을 피하기 위해 Early Return 패턴을 사용합니다.
- **Null Safety**: 반환 값이 null일 가능성이 있다면 `Optional<T>`를 사용하여 명시적으로 처리합니다.