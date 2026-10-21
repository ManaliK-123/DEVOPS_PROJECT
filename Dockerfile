# Production JRE runtime image for SmartRide Application
FROM eclipse-temurin:17-jre

# Install curl for health check probe
RUN apt-get update && \
    apt-get install -y --no-install-recommends curl && \
    rm -rf /var/lib/apt/lists/*

WORKDIR /app

# Create unprivileged application user
RUN groupadd -r smartride && useradd -r -g smartride -m -d /app smartride

# Copy pre-packaged WAR artifact built by CI pipeline
COPY target/smartride-app.war /app/smartride-app.war

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


