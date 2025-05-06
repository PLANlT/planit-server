# 1. 베이스 이미지
FROM gradle:8.6-jdk17 AS build
WORKDIR /app
COPY . .
RUN gradle build -x test --no-daemon

# 2. 실행 이미지
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY build/libs/planit-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
