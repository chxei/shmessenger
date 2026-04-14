# Project Backlog & Tasks

## 🐛 Bugs & Critical Integrations

- [ ] Implement `isActive` field validation natively during the login flow.
- [ ] find out whats wrong with swagger and why its showing non existant endpoints.
- [ ] proper ssl certificate.

## 🔐 Security & Operations

- [ ] **One-Line Container Execution**: Publish Docker image to a registry to allow consumers to run the container
  directly from the cloud with one command, without needing to pull the repository.
- [ ] Create missing **Authorities table** for definitive Role-Based Access Control (RBAC).
- [ ] Migrate `private.pem` JWT signing key to a secure Vault/Secret manager for production deployments.
- [ ] analyze code base and make sure everything is standardized.
- [ ] analyze code base and make sure everythink redundant is removed.
- [ ] analyze code base and make sure every endpoint is neccessary and no redundant endpoints exist.
- [ ] create unit tests for all endpoints and services.
- [ ] create integration tests for all endpoints and services.
- [ ] create end-to-end tests for all endpoints and services.
- [ ] create performance tests for all endpoints and services.
- [ ] create security tests for all endpoints and services.
- [ ] create workflow test wich follows the user journey and performes all available features.
- [ ] create similar workflow for postman or similar tools.

## 👤 User Management Features

- [ ] **Deactivate / Reactivate API**: Allow users to pause their accounts.
- [ ] **Forgot Password API**: Build email verification token system for resetting passwords.
- [ ] **Settings Infrastructure**: Create table for user profile settings (e.g., privacy toggles).
- [ ] **Media Management**: Implement upload endpoints for Profile Picture and Cover Photo files.

## 🤖 AI Features
- [ ] check out what instructions developers have for similar project and general. 

## ✅ Completed Tasks

- [x] **Secure Password Management**: Hooked `UserService.changePassword()` via `@PutMapping("/password")` and handled
  HTTP 401 interceptors.
- [x] **Global Exception Handlers**: Implemented `@ControllerAdvice` to gracefully manage SQLite conflicts.
- [x] **Docker Environments Refactor**: Pruned hardcoded mappings inside `docker-compose.yml` to rely structurally on
  `env_file`.
