# Image Dockerfile is your notebook
# Install Java
FROM eclipse-temurin:23-jdk AS builder

LABEL maintainer="calvinmilner"

ARG COMPILE_DIR=/compiledir

# How to build the application
# Create a directory /app and change directory into /app
# Outside of /app
# WORKDIR /app
WORKDIR ${COMPILE_DIR}

# Inside of /app
# Copy files over src dst
COPY .mvn .mvn
COPY mvnw .
COPY pom.xml .
COPY src src

# Build the application
RUN chmod a+x ./mvnw && ./mvnw package -Dmaven.test.skip=true

# If build is successful, jar will be in ./target

# How to run the application
# ENV SERVER_PORT=8080

# What port does the application need
# EXPOSE ${SERVER_PORT}

# Run the app
# Multi-stage does not require an ENTRYPOINT here
# ENTRYPOINT java -jar target/day18-0.0.1-SNAPSHOT.jar


# Second stage
FROM eclipse-temurin:23-jdk

ARG WORK_DIR=/app

WORKDIR ${WORK_DIR}

COPY --from=builder /compiledir/target/assessment-0.0.1-SNAPSHOT.jar vttp-paf-b4-assessment.jar

RUN apt update && apt install -y curl

# ENV SERVER_PORT=8080
ENV PORT=8080

EXPOSE ${PORT}

ENTRYPOINT java -jar vttp-paf-b4-assessment.jar

# Healthcheck
HEALTHCHECK --interval=60s --timeout=30s --start-period=120s --retries=3 CMD curl -s -f http://localhost:${PORT}/status || exit 1