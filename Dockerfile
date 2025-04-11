FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml /app/pom.xml
RUN mvn dependency:go-offline
COPY src /app/src
RUN mvn clean package -DskipTests

FROM openjdk:21-jdk-slim AS deploy

LABEL org.opencontainers.image.title="Hackaton Fiap File Process Consumer"
LABEL org.opencontainers.image.description="Hackaton Fiap File Process Consumer 8SOAT"
LABEL org.opencontainers.image.version="3.0.0"
LABEL org.opencontainers.image.url="https://github.com/fiap-8soat-tc-one/hackathon-fiap-file-process-consumer"
LABEL org.opencontainers.image.source="https://github.com/fiap-8soat-tc-one/hackathon-fiap-file-process-consumer"
LABEL org.opencontainers.image.authors="FIAP 8SOAT TEAM 32"
LABEL org.opencontainers.image.licenses="GNU General Public License v3.0"


WORKDIR /app
COPY --from=build /app/target/*.jar /app/hackaton-file-process-consumer.jar
ENTRYPOINT ["java", "-jar", "hackaton-file-process-consumer.jar"]
