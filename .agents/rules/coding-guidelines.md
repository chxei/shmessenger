---
trigger: always_on
description: CRITICAL: Apply to ALL tasks. Enforces global rules and Spring Controller conventions.
---

# Global Rules

Strictly follow these rules for all code:

1. **Logging (SLF4J)**:
    - NEVER use `System.out.println` or `java.util.logging`.
    - ALWAYS use **SLF4J**. Use proper levels (`debug`, `info`, `warn`, `error`).

2. **Formatting (IntelliJ Style)**:
    - Format code using **IntelliJ Default Java Formatter** with overrides.
    - **Overrides**: Keep short blocks, methods, lambdas, and classes single-line.
    - Indent with 4 spaces. Continue with 8 spaces.
    - Opening braces on same line.
    - Optimize imports. No wildcards.

3. **Documentation**:
    - Check `TODO.md` and `README.md` for related tasks.
    - Update `TODO.md` remove upon completion.
    - Update `README.md` for feature changes.

4. **Testing**:
    - Add tests for new functionality. Never leave code untested.

---

# Controller Rules

Follow these instructions for REST endpoints:

1. Use `@RestController`. Box endpoints with proper mapping annotations.
2. Delegate SQLite exceptions to global `@ControllerAdvice`.
3. Extract User ID from JWT context, not request payload.
4. Box responses in standard DTO wrappers.