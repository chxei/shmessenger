---
description: Run Gradle build, execute tests, manage Docker.
---

# Build & Test Workflow

Compile application and pass integration tests.

1. Ensure project root directory.
   // turbo
2. Check Docker container status. Run: `docker-compose ps`
   // turbo
3. Compile Spring application. Run: `./gradlew clean build -x test`
   // turbo
4. Run tests. Run: `./gradlew test`
5. Report results to user.
