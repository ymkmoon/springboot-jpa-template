Workflow: Code Refactoring

1. analyzer → Identify target files for refactoring.
2. refactor → Apply changes. **Pass only the list of modified files to review.**
3. review → Validate there are no functional changes (tests must pass). If FAIL, return to refactor with exact error logs.
4. docs → Update documentation only if public APIs or architecture changed.

Hand-off Rule: Keep communication strictly to what changed and why. Do not pass the entire file content between steps.
