FROM eclipse-temurin:24-jdk

RUN useradd -m myjavauser

WORKDIR /app

COPY target/*.jar app.jar

RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*
HEALTHCHECK --interval=10s --timeout=5s --start-period=5s --retries=3 \
  CMD ["curl", "-f", "http://localhost:8080" ]

EXPOSE 8080

USER myjavauser

ENTRYPOINT ["java", "-jar", "app.jar"]