AI Development Rules

Rule Priority

1 architecture.md
2 error-handling.md
3 repository-structure.md
4 api-style.md
5 coding-rules.md

Efficiency & Safety Rules (CRITICAL)

- Anti-Loop / Fail-Safe: If an agent fails to pass the `review` step twice for the same issue, IMMEDIATELY STOP the workflow and ask the user for clarification or help. Do not guess blindly.
- Minimum Context: Never read unrelated files. Use search tools first.
- No Speculation: Avoid speculative refactoring. Only fix what is explicitly requested.
- Conciseness: Keep conversational outputs and explanations concise. Let the code speak.
