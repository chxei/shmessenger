# Agent Config & Knowledge

The `.agents` directory holds rules, workflows, skills, and plugins for the AI assistant in `shmessenger`.

## Structure

- `workflows/`: Step-by-step instructions for repeatable processes. Auto-runnable.
- `skills/`: Specialized domain instructions. Teach specific patterns. Avoid generic code.
- `rules/`: Static global policies and code guidelines that should be adhered to.
- `plugins/`: MCP servers or tool integrations.

---

## Project State

Persistent context:

### 🛠️ Tech Stack

- **Backend:** Java 25, Spring Boot 3.5. Gradle for build.
- **Database:** SQLite. Handle conflicts via `@ControllerAdvice`.
- **Docs:** OpenAPI / Swagger (`springdoc`).
- **Utilities:** Lombok, `dotenv-kotlin`, Guava, Tinify (image compression).
- **Containerization:** Docker. Use `Dockerfile` and `docker-compose.yml`.
- **API Testing:** Postman (`.postman/`), Swagger UI.

### 📐 Architecture

- **Auth:** JWT with `private.pem`. OAuth2 Resource Server.
- **REST:** Spring annotations (`@PutMapping`).
- **Data:** Hibernate community dialects.
- **Packages:** standard (`config`, `controller`, `dto`, `entity`, `repository`, `service`, `utils`).
- **Errors:** `@ControllerAdvice`.

> **User:** Update this file to teach new things. Populate directories to extend capabilities.
