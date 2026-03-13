# AI Development Rules (The Constitution)

## 1. Rule Priority (Conflict Resolution)
1. architecture.md
2. error-handling.md
3. repository-structure.md
4. api-style.md
5. coding-rules.md

## 2. Efficiency & Safety Rules (CRITICAL)

- **Anti-Loop / Fail-Safe**: If an agent fails to pass the `review` step twice for the same issue, IMMEDIATELY STOP and ask the user for guidance. Never guess or retry blindly.
- **Surgical Updates (Exact Line Numbers)**: NEVER rewrite entire files. Rely strictly on **Exact Line Numbers** provided by the `analyzer`. Output ONLY the specific lines modified. This is the primary strategy to minimize token usage and avoid AI fatigue.
- **No Hallucination (Strict Prohibition)**: 
  - Never use non-existent libraries or invent versions.
  - Never guess API endpoints, file paths, or variable names. 
  - If unsure, you MUST verify via `ls`, `grep`, `find`, or by reading `build.gradle` before implementation.
- **Surgical Reading (sed -n)**: NEVER `cat` entire files. Use `sed -n '{start},{end}p'` to read only the necessary blocks identified by search tools.
- **Log Truncation**: When reporting errors, DO NOT output the full stack trace. Provide only the root cause and relevant "Caused by" blocks (Max 20 lines).
- **Minimum Context**: Do not read unrelated files. Use search tools (`grep`, `find`) to pinpoint targets before opening any file.
- **No Speculation**: Do not perform "drive-by" refactoring or future-proofing. Implement ONLY what is explicitly requested.
- **Test-Driven Stability**: Refactoring or logic changes without existing or new test coverage is strictly PROHIBITED.
- **Schema Awareness**: Any Entity modification MUST be synchronized with `src/main/resources/data.sql`.
- **Zero Conversational Filler**: Act like a high-performance CLI tool. Omit greetings or polite filler. Output only results, logs, and essential summaries.
