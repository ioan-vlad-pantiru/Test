# ----------- Stage 1: Build entire project from root -----------
FROM gradle:8.5-jdk17 AS build

WORKDIR /app

# Copy everything (including common + settings.gradle)
COPY . .

# Build only the :server jar
RUN ./gradlew :server:bootJar --no-daemon

# ----------- Stage 2: Run only the server app -----------
FROM eclipse-temurin:17

WORKDIR /app

# Copy the server jar only
COPY --from=build /app/server/build/libs/*.jar app.jar

EXPOSE 8080 9090

ENTRYPOINT ["java", "-jar", "app.jar"]