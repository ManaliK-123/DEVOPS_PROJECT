# Multi-stage Dockerfile for SmartRide Application

# Stage 1: Build the WAR artifact
FROM eclipse-temurin:17-jdk-jammy AS builder
WORKDIR /build

# Copy Maven Wrapper and POM
COPY pom.xml mvnw* ./
COPY .mvn .mvn/ 2>/dev/null || true
COPY src ./src

# Build artifact
RUN apt-get update && apt-get install -y maven && \
    mvn clean package -DskipTests

# Stage 2: Production JRE runtime image
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Create unprivileged application user
RUN groupadd -r smartride && useradd -r -g smartride -m -d /app smartride

# Copy built WAR artifact from builder stage
COPY --from=builder /build/target/smartride-app.war /app/smartride-app.war

# Set ownership
RUN chown -R smartride:smartride /app

USER smartride

# Expose HTTP port
EXPOSE 8080

# Environment variables
ENV SERVER_PORT=8080
ENV JAVA_OPTS="-Xms256m -Xmx512m -XX:+UseG1GC"

# Health Check Probe
HEALTHCHECK --interval=15s --timeout=5s --start-period=30s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# Launch Application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/smartride-app.war"]
