# syntax=docker/dockerfile:1.7

ARG JAVA_VERSION=25

FROM eclipse-temurin:${JAVA_VERSION}-jdk AS build
ARG SERVICE_DIR

WORKDIR /workspace

COPY ${SERVICE_DIR}/mvnw ./${SERVICE_DIR}/mvnw
COPY ${SERVICE_DIR}/pom.xml ./${SERVICE_DIR}/pom.xml
COPY ${SERVICE_DIR}/.mvn ./${SERVICE_DIR}/.mvn
COPY ${SERVICE_DIR}/src ./${SERVICE_DIR}/src

WORKDIR /workspace/${SERVICE_DIR}
RUN --mount=type=cache,target=/root/.m2 chmod +x mvnw && ./mvnw package -DskipTests

FROM eclipse-temurin:${JAVA_VERSION}-jre
ARG SERVICE_DIR

ENV JAVA_OPTS=""
WORKDIR /app

COPY --from=build /workspace/${SERVICE_DIR}/target/*.jar /app/app.jar

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
