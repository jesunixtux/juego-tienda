# syntax=docker/dockerfile:1.7

ARG JAVA_VERSION=25
ARG MAVEN_VERSION=3.9.11

FROM maven:${MAVEN_VERSION}-eclipse-temurin-${JAVA_VERSION} AS build
ARG SERVICE_DIR

ENV MAVEN_BUILD_OPTS="-DskipTests -B -Dmaven.wagon.http.retryHandler.count=5 -Dmaven.wagon.httpconnectionManager.ttlSeconds=120 -Dmaven.wagon.http.pool=false -Dmaven.artifact.threads=1"

WORKDIR /app

COPY ${SERVICE_DIR}/pom.xml ./pom.xml
COPY docker/maven-repository /opt/maven-repository

RUN --mount=type=cache,target=/root/.m2,sharing=locked \
    mkdir -p /root/.m2/repository \
    && cp -a /opt/maven-repository/. /root/.m2/repository/ \
    && (mvn -q -o dependency:go-offline -B >/dev/null 2>&1 || mvn -q dependency:go-offline -B -Dmaven.wagon.http.retryHandler.count=5 -Dmaven.artifact.threads=1 >/dev/null 2>&1 || true)

COPY ${SERVICE_DIR}/src ./src

RUN --mount=type=cache,target=/root/.m2,sharing=locked \
    mkdir -p /root/.m2/repository \
    && cp -a /opt/maven-repository/. /root/.m2/repository/ \
    && if mvn -o package ${MAVEN_BUILD_OPTS}; then \
        exit 0; \
    fi \
    && for attempt in 1 2 3; do \
        echo "Maven online build attempt ${attempt}/3 for ${SERVICE_DIR}"; \
        if mvn package ${MAVEN_BUILD_OPTS}; then \
            exit 0; \
        fi; \
        echo "Maven failed on attempt ${attempt}/3. Retrying after short pause..."; \
        sleep 10; \
    done; \
    echo "Maven build failed after 3 online attempts for ${SERVICE_DIR}"; \
    exit 1

FROM eclipse-temurin:${JAVA_VERSION}-jre
ARG SERVICE_DIR

ENV JAVA_OPTS=""
WORKDIR /app

COPY --from=build /app/target/*.jar /app/app.jar

ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar /app/app.jar"]