Workflow: Feature Development

1. planner → Create a concise task list.
2. analyzer → Identify files and dependencies based on the plan.
3. backend → Implement changes. **Pass only the list of modified files to review.**
4. review → Validate code and execution. If FAIL, return to backend with **exact error logs only**.
5. docs → Update relevant documentation based on the final approved code.
   **Skip if NO API surface changes** (no new endpoints, no new/modified DTOs, no Controller changes).

Hand-off Rule: Each agent must provide a "Brief Summary for Next Agent" (max 3 sentences) to minimize context accumulation.
