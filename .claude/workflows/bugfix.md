# Workflow: Bug Fix

**Goal**: Identify and resolve bugs with absolute minimum token usage and zero side-effects.

**Agent Sequence**: `analyzer` → `backend` → `review` (→ `docs` if API changes)

### Step-by-Step Execution

1. **`analyzer` (Root Cause Analysis)**
   - Identify the exact cause of the bug and target files using targeted search (`grep`, `find`).
   - NEVER read (`cat`) entire files unless absolutely necessary.
   - **Output**: The specific file paths and the exact lines causing the bug.

2. **`backend` (Surgical Fix)**
   - Implement the fix using minimal, surgical updates on the specified lines.
   - **Cost Saving**: Do not rewrite unchanged parts of the file.
   - **Output (CRITICAL)**: Pass ONLY the list of modified files and the **exact line numbers** to the `review` agent.

3. **`review` (Validation & Guardrails)**
   - Validate the modified sections. To save tokens, NEVER read (`cat`) the entire file. Use commands like `sed -n '{start},{end}p' {file}` to read only the affected context.
   - Run the specific tests for the modified domain (e.g., `./gradlew test --tests "*{Domain}*"`).
   - If FAIL: Return to `backend` with **exact error logs only**. 
     - *Token Saving Rule*: NEVER dump the full Spring Boot stack trace. Truncate logs using `grep -A 20 "Exception"` or provide only the exact failure reason.
   - If PASS: If API contracts were altered to fix the bug, instruct `docs` to update. Otherwise, conclude the workflow.

4. **`docs` (Postman Sync - ONLY IF NEEDED)**
   - Run ONLY IF the bug fix altered the API Request/Response shape.
   - Surgically update `postman/BE_*.json` using `jq` or a minimal Python script to preserve JSON integrity. NEVER append.

### General Handoff Rule (CRITICAL)
- **Context Pruning**: Each agent MUST provide a "Brief Summary for Next Agent" (maximum 3 sentences) at the end of their turn. Do not accumulate or repeat previous context to prevent token waste.
