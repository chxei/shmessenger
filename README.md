# shmessenger

[![Java CI with Gradle](https://github.com/chxei/shmessenger/actions/workflows/gradle.yml/badge.svg?branch=development)](https://github.com/chxei/shmessenger/actions/workflows/gradle.yml)
![GitHub top language](https://img.shields.io/github/languages/top/chxei/shmessenger)
![GitHub code size in bytes](https://img.shields.io/github/languages/code-size/chxei/shmessenger)
![GitHub tag (latest by date)](https://img.shields.io/github/v/tag/chxei/shmessenger)
![GitHub last commit](https://img.shields.io/github/last-commit/chxei/shmessenger)
![GitHub commit activity](https://img.shields.io/github/commit-activity/y/chxei/shmessenger)

## 🚀 Getting Started

You can run the application directly via Gradle, or optionally containerize it with Docker.

### Local Execution (Gradle)
```bash
./gradlew bootRun
```

### Docker (Direct Build)
```bash
docker build -t shmessenger .
docker run -p 8080:8080 shmessenger
```

### Docker Compose
```bash
docker compose up --build
```

## 📖 API Documentation

The backend utilizes OpenAPI (Swagger) to document all endpoints automatically. 
Once the application is running, you can interact with the API interface at:
👉 **[Swagger UI](https://localhost:8080/swagger-ui/index.html)**

# Known Build Warnings

When running tests or the application, you may see the following harmless JVM warnings in your console:

1. **`WARNING: A restricted method in java.lang.System has been called`**
   - **Why it happens:** The SQLite JDBC driver uses native C libraries via `System.load()`. Java 22+ logs a warning whenever native libraries are loaded without explicit permission flags.
   - **Why it's normal:** Standard SQLite behavior. It's completely harmless and does not affect the app.

2. **`Java HotSpot(TM) 64-Bit Server VM warning: Sharing is only supported for boot loader classes...`**
   - **Why it happens:** Spring Boot uses dynamic library modification (like ByteBuddy for Mockito checks) which disables the JVM's "Class Data Sharing" (CDS) optimization for application classes.
   - **Why it's normal:** Standard behavior for Spring implementations with mock testing setups.
