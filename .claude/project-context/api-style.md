# API Style & Request Validation

**Goal**: Define a consistent RESTful API structure and ensure all incoming data is strictly validated before processing.

## 1. Resource & Path Naming
- **Context path**: `/template` (e.g., `https://api.example.com/template/...`)
- **Resource-oriented**: Use nouns for resources, not verbs (e.g., `/template/admins/{id}`).
- **Hyphen-case**: Use kebab-case for multi-word paths (e.g., `/template/user-profiles`).

## 2. Request Validation (CRITICAL)
- **Mandatory @Valid**: Every `@RequestBody` or `@ModelAttribute` DTO in a `@RestController` MUST be annotated with `@Valid` or `@Validated`.
- **DTO Constraints**: Use `jakarta.validation.constraints` (or `javax`) to define rules within the DTO inner classes.
  - *Example*: `@NotBlank`, `@Size`, `@Min`, `@Email`.
- **Validation Error Handling**: 
  - When validation fails (`MethodArgumentNotValidException`), the `GlobalExceptionHandler` MUST catch it.
  - **Return Code**: Always return `ResponseCode.BAD_REQUEST` (or its equivalent in your project) to indicate client-side input errors.
  - **Details**: Include the specific field-level errors in the `message` or `data` block of the `ApiResponse`.

## 3. Response Format (The Envelope)
- All responses MUST use the `ApiResponse<T>` wrapper.
  

{
  "timestamp": "2026-03-13T16:59:27",
  "code": "20000000",
  "message": "성공",
  "data": { ... }
}

## 4. Operational Rules for Agents
* **Minimum Data Exposure**: Implement ONLY the fields requested. Do not expose internal IDs or sensitive metadata unless explicitly told.
* **Contract Integrity**: NEVER change an existing API's path, method, or response field name. If a change is needed, the `planner` must explicitly approve it.
* **HTTP Method Usage**:
    * **GET**: Resource retrieval (No side effects).
    * **POST**: Resource creation.
    * **PUT**: Full resource update.
    * **PATCH**: Partial resource update (Preferred for efficiency).
    * **DELETE**: Logical deletion (Soft delete via `isActive=false`).

## 5. Security & Auth Awareness
* **Method-Level Security**: New endpoints SHOULD be evaluated for `@PreAuthorize("hasAnyRole('...')")`.
* **No Manual Parsing**: Rely on `SecurityContextHolder` or `@AuthenticationPrincipal` to get user information; do not pass `userId` via PathVariables unless it's for an admin feature.
