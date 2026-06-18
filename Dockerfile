# syntax=docker/dockerfile:1.7

ARG JAVA_VERSION=25
ARG MAVEN_VERSION=3.9.11

FROM maven:${MAVEN_VERSION}-eclipse-temurin-${JAVA_VERSION} AS build
ARG SERVICE_DIR

WORKDIR /app

COPY ${SERVICE_DIR}/pom.xml ./pom.xml
COPY docker/maven-repository /opt/maven-repository

RUN --mount=type=cache,target=/root/.m2,sharing=locked \
    mkdir -p /root/.m2/repository \
    && cp -a /opt/maven-repository/. /root/.m2/repository/ \
    && mvn -q -o dependency:go-offline -B >/dev/null 2>&1 || true

COPY ${SERVICE_DIR}/src ./src

RUN --mount=type=cache,target=/root/.m2,sharing=locked \
    mkdir -p /root/.m2/repository \
    && cp -a /opt/maven-repository/. /root/.m2/repository/ \
    && (mvn -o package -DskipTests -B || mvn package -DskipTests -B)

FROM eclipse-temurin:${JAVA_VERSION}-jre
ARG SERVICE_DIR

ENV JAVA_OPTS=""
WORKDIR /app

COPY --from=build /app/target/*.jar /app/app.jar

ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar /app/app.jar"]
