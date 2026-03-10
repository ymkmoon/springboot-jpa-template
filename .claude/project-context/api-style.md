API Style

- Context path: `/template`.
- Resource-oriented naming (e.g. `/template/auth/sign-in`, `/template/admins/{id}`).

Response Format
`ApiResponse<T>`: `timestamp`, `code`, `message`, `data`.
  
{
"timestamp": "...",
"code": "20000000",
"message": "성공",
"data": {}
}
  
Rules
- **Strict Scope**: Do not create generic CRUD endpoints unless explicitly requested. Implement ONLY the APIs required by the current task.
- **Contract Integrity**: Do not break existing API contracts.
