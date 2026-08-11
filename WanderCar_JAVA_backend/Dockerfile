# syntax=docker/dockerfile:1

# =====================================================================
#  WanderCar — Java backend (Spring Boot 3.3, Java 17)
#  Build context: ./WanderCar_JAVA
# =====================================================================

# ---------------------------------------------------------------------
# Stage 1 — build the executable jar
# Java 17 to match <java.version>17</java.version> in pom.xml.
# ---------------------------------------------------------------------
FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /build

# pom.xml first so the dependency layer is reused whenever only src changes.
COPY pom.xml ./

# Best-effort warm-up: this layer is cached against pom.xml alone, so a
# source-only change skips dependency resolution entirely. '|| true' because
# go-offline is a cache primer, not a build step — if it can't pre-resolve
# something, the package step below resolves it normally.
RUN --mount=type=cache,target=/root/.m2 \
    mvn -B -DskipTests dependency:go-offline || true

COPY src ./src

RUN --mount=type=cache,target=/root/.m2 \
    mvn -B -DskipTests clean package

# ---------------------------------------------------------------------
# Stage 2 — runtime
# Same Ubuntu Jammy base family as the .NET image so both services share
# one OS layer lineage, one package manager, and one healthcheck tool.
# ---------------------------------------------------------------------
FROM eclipse-temurin:17-jre-jammy AS runtime

LABEL org.opencontainers.image.title="wandercar-java-backend" \
      org.opencontainers.image.description="WanderCar fleet management backend (Spring Boot)" \
      org.opencontainers.image.version="1.0.0"

# curl + grep back the healthcheck below; the app user matches the
# pre-existing non-root 'app' user in the .NET runtime image.
RUN apt-get update \
 && apt-get install -y --no-install-recommends curl \
 && rm -rf /var/lib/apt/lists/* \
 && groupadd --system app \
 && useradd --system --gid app --create-home --home-dir /home/app app

WORKDIR /app

COPY --from=build /build/target/fleman-backend-*.jar /app/app.jar

# logs/ is written by logback (see application-prod.properties logging.file.name).
RUN mkdir -p /app/logs && chown -R app:app /app

USER app

EXPOSE 8080

ENV JAVA_OPTS="-XX:MaxRAMPercentage=75.0" \
    SERVER_PORT=8080 \
    TZ=Asia/Kolkata

# Hits the real seeded endpoint rather than /actuator/health: Spring opens the
# HTTP port before CommandLineRunners finish, so only a non-empty /api/states
# proves DataSeeder actually wrote its fixture data into MySQL.
#
# start-interval probes every 5s while booting so compose's depends_on gate
# releases promptly; once healthy it drops to 5m. The app logs every request,
# so a short steady-state interval floods `docker compose logs` forever.
HEALTHCHECK --interval=5m --timeout=5s --start-period=120s --start-interval=5s --retries=3 \
  CMD curl -fsS http://localhost:8080/api/states | grep -q Maharashtra || exit 1

ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar /app/app.jar"]
