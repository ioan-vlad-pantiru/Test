# ----------- Stage 1: Build entire project from root -----------
FROM gradle:8.5-jdk17 AS build

# Set working directory
WORKDIR /app

# Copy all project files
COPY . .

# Build only the :server jar
RUN ./gradlew :server:bootJar --no-daemon

# ----------- Stage 2: Run only the server app -----------
FROM eclipse-temurin:17

# Set working directory
WORKDIR /app

# Copy the server jar only
COPY --from=build /app/server/build/libs/*.jar app.jar

# Expose necessary ports
EXPOSE 8080
EXPOSE 9090

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]