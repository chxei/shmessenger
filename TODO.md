# Project Backlog & Tasks

## 🐛 Bugs & Critical Integrations
- [ ] Implement `isActive` field validation natively during the login flow.

## 🔐 Security & Operations
- [ ] Create missing **Authorities table** for definitive Role-Based Access Control (RBAC).
- [ ] Migrate `private.pem` JWT signing key to a secure Vault/Secret manager for production deployments.

## 👤 User Management Features
- [ ] **Deactivate / Reactivate API**: Allow users to pause their accounts.
- [ ] **Forgot Password API**: Build email verification token system for resetting passwords.
- [ ] **Settings Infrastructure**: Create table for user profile settings (e.g., privacy toggles).
- [ ] **Media Management**: Implement upload endpoints for Profile Picture and Cover Photo files.

## ✅ Completed Tasks
- [x] **Secure Password Management**: Hooked `UserService.changePassword()` via `@PutMapping("/password")` and handled HTTP 401 interceptors.
- [x] **Global Exception Handlers**: Implemented `@ControllerAdvice` to gracefully manage SQLite conflicts.
- [x] **Docker Environments Refactor**: Pruned hardcoded mappings inside `docker-compose.yml` to rely structurally on `env_file`.
- [x] **One-Line Container Execution**: Verified the `--env-file` parameter requirement.
