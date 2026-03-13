# Workflow: Code Refactoring

**Goal**: Improve maintainability and reduce technical debt with zero functional changes and absolute minimum token usage.

**Agent Sequence**: `analyzer` → `refactor` → `review` (→ `docs` if architecture changed)

### Step-by-Step Execution

1. **`analyzer` (Target & Coverage Analysis)**
   - Identify target files and the **exact line numbers** to refactor using `grep` and `find`.
   - Verify if reliable unit tests exist for the target logic.
   - **Output**: The specific file paths, exact lines, and test coverage status.

2. **`refactor` (Surgical Improvement)**
   - Apply refactoring surgically on the specified lines. 
   - **Safety Rule**: If tests do not exist, you MUST write them first before altering logic.
   - **Output (CRITICAL)**: Pass ONLY the list of modified files and the **exact line numbers** to the `review` agent.

3. **`review` (Strict Regression Guard)**
   - Validate the modified sections. To save tokens, NEVER read (`cat`) the entire file. Use commands like `sed -n '{start},{end}p' {file}` to read only the updated logic.
   - Run the specific tests for the modified domain (e.g., `./gradlew test --tests "*{Domain}*"`).
   - If FAIL: Return to `refactor` with **truncated error logs only** (Max 20 lines). NEVER dump full stack traces.
   - If PASS: If architectural structures (e.g., file locations) changed, instruct `docs` to update the map. Otherwise, conclude the workflow.

4. **`docs` (Map Sync - ONLY IF NEEDED)**
   - Run ONLY IF file structures or architectural boundaries changed.
   - Update `project-map.md` accurately.
   - (Note: Public APIs should generally NOT change during refactoring. If they unexpectedly do, you MUST surgically update `postman/BE_*.json` using `jq`).

### General Handoff Rule (CRITICAL)
- **Context Pruning**: Each agent MUST provide a "Brief Summary for Next Agent" (maximum 3 sentences) at the end of their turn. Do not accumulate or repeat previous file contents to prevent token waste.
