# Stage 1: Build (but skip the Maven part since we built locally)
FROM eclipse-temurin:17-jre AS builder
WORKDIR /app
COPY target/string_analyzer-0.0.1-SNAPSHOT.jar app.jar

# Stage 2: Run
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=builder /app/app.jar app.jar
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]
