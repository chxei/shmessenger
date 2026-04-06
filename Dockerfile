# Build stage
FROM eclipse-temurin:25-jdk-alpine AS build
WORKDIR /app

# Copy gradle files
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .
COPY gradle.properties .

# Give execution permission to gradlew
RUN chmod +x gradlew

# Download dependencies
RUN ./gradlew dependencies --no-daemon

# Copy source code
COPY src src

# Build the application
RUN ./gradlew bootJar --no-daemon

# Run stage
FROM eclipse-temurin:25-jre-alpine
WORKDIR /app

# Copy the jar from build stage
COPY --from=build /app/build/libs/*.jar app.jar

# Expose port (default for Spring Boot is 8080, but app-server.properties mentions 443)
EXPOSE 8080 443

# Entry point
ENTRYPOINT ["java", "-jar", "app.jar"]
