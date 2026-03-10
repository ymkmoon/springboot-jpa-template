AI Development Rules

Rule Priority

1 architecture.md
2 error-handling.md
3 repository-structure.md
4 api-style.md
5 coding-rules.md

Efficiency & Safety Rules (CRITICAL)

- **Anti-Loop / Fail-Safe**: If an agent fails to pass the `review` step twice for the same issue, IMMEDIATELY STOP the workflow and ask the user for clarification or help. Do not guess blindly.
- **Surgical Updates (Token Saving)**: NEVER rewrite entire files. Use precise line modifications or unified diffs. Minimize conversational overhead and keep explanations concise.
- **Log Truncation**: When reporting errors or test failures, do not output the full stack trace. Extract and provide only the root cause and the most relevant "Caused by" blocks (Max 20 lines).
- **Minimum Context**: Never read unrelated files. Use search tools (`grep`, `ls`, `find`) first to identify target files before reading their content.
- **No Speculation**: Avoid speculative refactoring or future-proofing. Only implement what is explicitly requested in the current task.
- **Test-Driven Stability**: Refactoring or logic changes are prohibited without existing or newly written test coverage. Ensure all modified logic is validated by Unit/Integration tests.
- **Schema Awareness**: If a task involves modifying Entity fields, you MUST ensure synchronization with the database schema (e.g., migration scripts or `data.sql`) and notify the user.
- **Conciseness**: Keep conversational outputs and explanations concise. Let the code speak.
