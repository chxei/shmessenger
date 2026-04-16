# Project Backlog & Tasks

## 🐛 Bugs & Critical Integrations

- [ ] proper ssl certificate.

## 🔐 Security & Operations

- [ ] **One-Line Container Execution**: Publish Docker image to a registry to allow consumers to run the container
  directly from the cloud with one command, without needing to pull the repository.
- [ ] Create a missing **Authorities table** for definitive Role-Based Access Control (RBAC).
- [ ] Migrate `private.pem` JWT signing key to a secure Vault/Secret manager for production deployments.
- [ ] analyze the code base and make sure everything redundant is removed.
- [ ] analyze the code base and make sure every endpoint is necessary and no redundant endpoints exist.
- [ ] create unit tests for all endpoints and services.
- [ ] create integration tests for all endpoints and services.
- [ ] create end-to-end tests for all endpoints and services.
- [ ] create performance tests for all endpoints and services.
- [ ] create security tests for all endpoints and services.
- [ ] create a workflow test which follows the user journey and performs all available features.
- [ ] create a similar workflow for postman or similar tools.
- [ ] create a CI/CD pipeline for automated testing and deployment.
- [ ] integrate OpenRewrite when it supports spring boot 4.

## 👤 User Management Features

- [ ] **Deactivate / Reactivate API**: Allow users to pause their accounts.
- [ ] **Forgot Password API**: Build email verification token system for resetting passwords.
- [ ] **Settings Infrastructure**: Create table for user profile settings (e.g., privacy toggles).
- [ ] **Media Management**: Implement upload endpoints for Profile Picture and Cover Photo files.

## 🤖 AI Features
- [ ] check out what instructions developers have for similar project and general. 
