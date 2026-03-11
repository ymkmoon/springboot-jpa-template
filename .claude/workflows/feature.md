Workflow: Feature Development

1. planner → Create a concise task list.
2. analyzer → Identify files and dependencies based on the plan.
3. backend → Implement changes. **Pass only the list of modified files to review.**
4. review → Validate code and execution. If FAIL, return to backend with **exact error logs only**.
5. docs → Run ONLY IF any of the following apply:
   - New REST endpoint added
   - Existing Controller response shape changed
   - New domain directory created
   Skip docs if the change is limited to internal Service logic or QueryDSL / JPA query modifications only.

Hand-off Rule: Each agent must provide a "Brief Summary for Next Agent" (max 3 sentences) to minimize context accumulation.
