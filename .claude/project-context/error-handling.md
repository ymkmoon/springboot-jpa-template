Error Handling

- Never ignore errors.
- Wrap errors with context.

Example

All exceptions are handled centrally by `GlobalExceptionHandler`. For business errors, throw `BusinessException` with a `ResponseCode`. Response envelope is `ApiResponse<T>` (timestamp, code, message, data).

throw new BusinessException(ResponseCode.ADMIN_NOT_FOUND);

- Define domain errors when needed.
