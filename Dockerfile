FROM eclipse-temurin:21-jdk AS build

WORKDIR /build

COPY .mvn/ .mvn/
COPY mvnw pom.xml ./

RUN ./mvnw --batch-mode --no-transfer-progress -DskipTests dependency:go-offline

COPY src/ src/

RUN ./mvnw --batch-mode --no-transfer-progress -DskipTests package

FROM eclipse-temurin:21-jre

RUN apt-get update \
    && apt-get install -y --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/* \
    && useradd -m myjavauser

WORKDIR /app

COPY --from=build /build/target/*.jar app.jar

EXPOSE 8080

USER myjavauser

HEALTHCHECK --interval=10s --timeout=5s --start-period=40s --retries=3 \
    CMD curl -fsS http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]
