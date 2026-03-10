# Project Map (Source of Truth for Search)

Goal: Minimize repository-wide scanning by providing a structural blueprint.

**Mandatory Search Protocol**
- ANY agent searching for files MUST read this file BEFORE executing `find` or `ls`.
- Constrain all search commands to the specific `{domain}` package identified here.

Feature Mapping Rule
Features are grouped by domain package under `src/main/java/com/example/template/{domain}/`.
- Delivery: `{domain}/{Domain}Controller.java`
- Usecase: `{domain}/{Domain}Service.java`, `{domain}/{Domain}ServiceImpl.java`
- Repository: `{domain}/{Domain}Repository.java`

Shared Context
- Global Exceptions: `exception/`
- Constants: `constants/ResponseCode.java`
- Configs: `config/`

Search Strategy (Strict)
1. Identify the domain from the task.
2. Target the specific directory: `src/main/java/com/example/template/{domain}/`.
3. Use `ls` only within that directory. Do not use recursive `ls -R` on the root.
