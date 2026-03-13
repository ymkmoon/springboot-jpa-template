# Error Handling Rules (Java/Spring Boot)

**Goal**: Ensure consistent error responses and simplified debugging through strict exception chaining and centralized handling.

## 1. Core Principles
- **No Hallucination (CRITICAL)**: NEVER invent new error codes. You MUST search `constants/ResponseCode.java` to find the existing business error code that matches the situation.
- **Centralized Management**: All exceptions MUST be handled by the `GlobalExceptionHandler`. Do not implement local try-catch blocks for standard business flow unless specific recovery logic is required.

## 2. Throwing Exceptions
- **Standard Business Error**: Use `BusinessException` with a predefined `ResponseCode`.
  - *Example*: `throw new BusinessException(ResponseCode.USER_NOT_FOUND);`
- **Exception Chaining (Validation & Safety)**: When catching a lower-level exception (e.g., `IOException`, `SQLException`) to wrap it into a `BusinessException`, ALWAYS pass the original cause to preserve the stack trace.
  - *Correct*: `throw new BusinessException(ResponseCode.FILE_PROCESSING_ERROR, e);`
  - *Incorrect*: `throw new BusinessException(ResponseCode.FILE_PROCESSING_ERROR); // Context lost`

## 3. Analyzer Search Tip (Cost Saving)
- **Precise Code Lookup**: When the `analyzer` needs to find a `ResponseCode`, do not `cat` the entire file. Use `grep` with the `-E` flag to identify the code name and its associated message.
  - *Command*: `grep -E "NAME_OF_ERROR|CODE_VALUE" src/main/java/com/example/template/constants/ResponseCode.java`
  - *Verification*: Confirm the message matches the business intent before implementation.

## 4. Response Integrity
- **Envelope Consistency**: Every error response MUST adhere to the `ApiResponse<T>` envelope defined in `api-style.md`.
- **Minimal Exposure**: In production profiles, ensure the `GlobalExceptionHandler` does not leak internal stack traces to the client. Only the `code`, `message`, and `timestamp` should be visible.
