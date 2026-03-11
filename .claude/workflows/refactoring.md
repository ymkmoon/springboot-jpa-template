Workflow: Code Refactoring

1. analyzer → Identify target files for refactoring.
2. refactor → Apply changes. **Pass only the list of modified files to review.**
3. review → Validate there are no functional changes (tests must pass). If FAIL, return to refactor with exact error logs.
4. docs → Run ONLY IF any of the following apply:
   - Existing Controller response shape changed
   - New domain directory created
   Skip docs if the change is limited to internal Service logic or QueryDSL / JPA query modifications only.

Hand-off Rule: Keep communication strictly to what changed and why. Do not pass the entire file content between steps.
