Error Handling Rules

- **No Hallucination**: Never invent new error codes. ALWAYS search `constants/ResponseCode.java` to find the appropriate business error code.
- **Centralized Handling**: All exceptions must be handled by `GlobalExceptionHandler`.
- **Business Exceptions**: Use `throw new BusinessException(ResponseCode.NAME);` for expected domain errors.
- **Context Wrapping**: When catching exceptions, wrap them with sufficient context to simplify debugging.
- **Response Format**: Ensure every error response adheres to the `ApiResponse<T>` envelope defined in `api-style.md`.
