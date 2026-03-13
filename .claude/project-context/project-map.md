# Project Map (The Source of Truth)

**Goal**: Minimize repository-wide scanning by providing a structural blueprint. This file is the primary context for the `analyzer` agent.

---

## 1. Self-Update Rule (CRITICAL for `docs` Agent)
- **Priority Zero**: If a task involves adding a new `{domain}`, moving directories, or creating major shared components, the `docs` agent MUST update this file **BEFORE** any other documentation task.
- **Accuracy**: An outdated map leads to `analyzer` failure. Ensure all paths and domain mappings reflect the current `HEAD` of the repository.

## 2. Mandatory Search Protocol
- **Search First**: EVERY agent searching for files MUST read this file BEFORE executing `find` or `ls`.
- **Constraint**: All search commands must be restricted to the specific `{domain}` package identified here.

## 3. Feature Mapping Standards
Business domains are located under `src/main/java/com/example/template/{domain}/`.
- **Delivery**: `{domain}/{Domain}Controller.java`
- **Usecase**: `{domain}/{Domain}Service.java`, `{domain}/{Domain}ServiceImpl.java`
- **Repository**: `{domain}/{Domain}Repository.java`, `{domain}/{Domain}RepositoryCustom.java`

## 4. Shared Context Mapping
- **Global Exceptions**: `exception/`
- **Constants**: `constants/ResponseCode.java`
- **Configs**: `config/` (Security, QueryDSL, JPA, Redis)
- **Infrastructure**: `security/`, `filter/`, `aop/`

## 5. API Documentation (Postman)
- **Directory**: `postman/`
- **Naming**: `BE_{Number}_{Domain}.postman_collection.json`
- **Sync Rule**: Every Controller API change must be surgically reflected in these JSON files using `jq`.

## 6. Search Strategy (The Surgical Strike)
1.  **Identify**: Determine the domain from the user request.
2.  **Target**: Use the map to find the exact package: `src/main/java/com/example/template/{domain}/`.
3.  **Execute**: Use `ls` or `grep` ONLY within that targeted directory. No recursive root scanning.
