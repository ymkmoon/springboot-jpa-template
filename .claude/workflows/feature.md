# Workflow: Feature Development

**Goal**: Implement new features safely with surgical precision, minimum token usage, and zero regression.

**Agent Sequence**: `planner` → `analyzer` → `backend` → `review` → `docs`

### Step-by-Step Execution

1. **`planner` (Execution & Test Planning)**
   - Create a concise execution plan.
   - Anticipate impacted domains and assign initial test targets.

2. **`analyzer` (Precision Targeting)**
   - Identify target files and find the **exact line numbers** to modify using `grep`. 
   - Expand the `planner`'s test targets if hidden dependencies are found.

3. **`backend` (Surgical Implementation)**
   - Implement changes surgically on the specified lines. 
   - **Output (CRITICAL)**: Pass the modified files AND **exact line numbers** to `review` for targeted inspection.

4. **`review` (Validation & Guardrails)**
   - Validate the code using `sed` on the specific modified lines. NEVER read entire files.
   - Run the targeted tests. If FAIL, return to `backend` with **truncated error logs only** (Max 20 lines). 
   - If structural changes occurred, explicitly instruct `docs` to update the map.

5. **`docs` (Documentation & Postman Sync)** - Run ONLY IF any of the following apply:
     - New REST endpoint added (**MUST surgically inject into the correct array in `postman/BE_*.json` using `jq`. NEVER append.**)
     - Existing resolver or controller response shape changed (**MUST surgically update `postman/BE_*.json` using `jq` or a minimal Python script to preserve JSON integrity.**)
     - API Request information changed (e.g., Request Body DTO, Query Parameters, Headers) (**MUST surgically update `postman/BE_*.json` using `jq` or Python.**)
     - New domain directory created
   - Skip `docs` if the change is limited to internal Service logic or QueryDSL / JPA query modifications only.

### General Handoff Rule (CRITICAL)
- **Context Pruning**: Each agent MUST provide a "Brief Summary for Next Agent" (maximum 3 sentences) at the end of their turn to minimize context accumulation and token waste.
