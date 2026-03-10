# Coding Rules

## 1. Core Principles
- **Readability Over Brevity**: 코드는 간결함보다 가독성과 유지보수성을 우선합니다.
- **No Clever Tricks**: 이해하기 어려운 복잡한 기교나 한 줄짜리 트릭을 지양합니다.
- **Small Functions**: 하나의 메서드는 하나의 일만 수행하며, 가급적 20라인 이내로 유지합니다.
- **Strict Structure**: 정해진 패키지 구조와 레이어 아키텍처를 엄격히 준수합니다.

## 2. Dependency Injection (DI) & Lombok
- **Constructor Injection Only**: 모든 의존성 주입은 생성자 주입 방식을 사용합니다.
- **RequiredArgsConstructor**: Lombok의 `@RequiredArgsConstructor`를 사용하여 생성자를 자동 생성합니다.
- **Final Fields**: 주입받는 모든 필드는 반드시 `private final`로 선언하여 불변성을 보장합니다.
- **NO @Autowired**: 필드에 직접 `@Autowired`를 사용하는 것을 엄격히 금지합니다. (에이전트 회귀 방지)

## 3. Database & Querying
- **QueryDSL (V3) First**: 새로운 조회 로직은 Native SQL(V1)이나 JPQL(V2)보다 QueryDSL(V3) 사용을 우선합니다.
- **JPA Conventions**: 
  - 모든 엔티티는 `BaseEntity`를 상속받아 공통 필드(`createdAt`, `updatedAt` 등)를 관리합니다.
  - 엔티티 이름은 `*Entity` 접미사를 사용합니다.
- **Soft Delete**: 삭제 시 실제 로직 삭제 대신 `isActive = false`를 통한 논리 삭제를 기본으로 합니다.

## 4. Layered Architecture Patterns
- **Service Layer**: 인터페이스(`*Service`)와 구현체(`*ServiceImpl`)를 분리합니다.
- **DTO Usage**: 
  - 엔티티를 API 외부로 직접 노출하지 않습니다.
  - DTO는 `*Dto` 클래스 내부에 static inner class 구조로 정의하여 관리합니다.
- **ReadOnly Transactions**: 조회 전용 서비스 메서드에는 `@Transactional(readOnly = true)`를 명시하여 성능과 가독성을 높입니다.

## 5. Error Handling & Response
- **BusinessException**: 비즈니스 로직 에러는 정의된 `ResponseCode`와 함께 `BusinessException`을 던집니다.
- **Global Handler**: 모든 예외는 `GlobalExceptionHandler`에서 중앙 제어합니다.
- **Response Envelope**: 모든 API 응답은 공통 규격인 `ApiResponse<T>`로 래핑하여 반환합니다.

## 6. Logging & Style
- **Slf4j**: 로그 기록 시 `System.out.println` 대신 `@Slf4j`를 사용합니다.
- **Early Return**: 중첩된 if문을 피하기 위해 Early Return 패턴을 권장합니다.
- **Optional**: Null 가능성이 있는 반환 타입은 `Optional<T>`를 사용하여 명시적으로 처리합니다.

## 7. Comments & Logging
- **Self-Documenting Code**: 필드와 메서드명으로 의도를 충분히 설명하고, 주석은 '무엇'이 아닌 '왜(Why)'를 설명할 때만 사용합니다.
- **Log Levels**: 
  - `INFO`: 비즈니스 프로세스 시작/종료.
  - `DEBUG`: 파라미터 정보 및 쿼리 조건.
  - `ERROR`: 예외 발생 및 시스템 오류 (Stack trace 포함).
- **No Cleanup Comments**: `TODO`, `FIXME` 등 나중에 지워야 할 주석을 남발하지 않습니다.

## 8. Performance Awareness
- **Pagination**: 목록 조회 API는 항상 `Pageable`을 사용하거나 제한된 개수만 반환합니다.
- **N+1 Problem**: Fetch Join이나 Entity Graph를 사용하여 연관 엔티티 조회 시 N+1 문제를 사전에 차단합니다.
