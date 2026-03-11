Workflow: Bug Fix

1. analyzer → Identify the exact cause of the bug and target files.
2. backend → Implement the fix. **Run `./gradlew compileJava` before handoff. If FAIL, fix immediately. Pass only the list of modified files to review.**
3. review → Validate code and execution. If FAIL, return to backend with **exact error logs only**.

Hand-off Rule: Each agent must provide a "Brief Summary for Next Agent" (max 3 sentences) to minimize context accumulation.
