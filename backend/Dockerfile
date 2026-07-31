# syntax=docker/dockerfile:1

FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn -q -DskipTests package

FROM eclipse-temurin:21-jre
WORKDIR /app
RUN apt-get update \
  && apt-get install -y --no-install-recommends curl \
  && rm -rf /var/lib/apt/lists/*
COPY --from=build /app/target/matriculas-backend-0.0.1-SNAPSHOT.jar /app/app.jar
EXPOSE 8080
HEALTHCHECK --interval=15s --timeout=5s --retries=20 --start-period=40s \
  CMD curl -fsS http://localhost:8080/api-docs >/dev/null || exit 1
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
