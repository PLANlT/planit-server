## 🚀 Planit 프로젝트 실행 가이드

이 문서는 Planit 프로젝트를 로컬 및 Docker 환경에서 실행하는 방법과 테스트 환경 설정에 대해 안내합니다.

## 🛠️ 프로젝트 구성

백엔드: Spring Boot 3 (JDK 17)

빌드 도구: Gradle

DB:

로컬: Docker 기반 MySQL 8.0

테스트: H2 (in-memory)

## 🧩 환경 변수 설정

.env.local (로컬 실행용):

SPRING_PROFILES_ACTIVE=prod
SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/planit
SPRING_DATASOURCE_USERNAME=planituser
SPRING_DATASOURCE_PASSWORD=planitpass

.env.prod (Docker Compose 실행용):

SPRING_PROFILES_ACTIVE=prod
SPRING_DATASOURCE_URL=jdbc:mysql://mysql:3306/planit
SPRING_DATASOURCE_USERNAME=planituser
SPRING_DATASOURCE_PASSWORD=planitpass

## 🐳 Docker로 실행하기

1. 컨테이너 실행

docker-compose up --build -d

2. 실행 확인

docker ps

mysql, app 컨테이너가 떠 있어야 함

3. 로그 확인

docker logs app

## 💻 로컬에서 실행하기 (도커 MySQL 사용)

1. Docker로 MySQL만 실행

docker-compose up -d mysql

2. 로컬에서 Gradle 실행 (환경변수 로드)

export $(cat .env.local | xargs)
./gradlew bootRun

또는 IntelliJ > Run Configuration > Environment variables에 다음 추가:

SPRING_PROFILES_ACTIVE=prod;SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/planit;SPRING_DATASOURCE_USERNAME=planituser;SPRING_DATASOURCE_PASSWORD=planitpass

## ✅ 테스트 환경 (TDD용)

테스트 시 H2 인메모리 DB 사용

프로파일: test

application-test.yml 설정 요약:

spring:
  datasource:
    driver-class-name: org.h2.Driver
    url: jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;MODE=MYSQL
    username: sa
    password:
  jpa:
    hibernate:
      ddl-auto: create-drop

테스트 코드 예시:

@SpringBootTest
@ActiveProfiles("test")
class MyServiceTest {
    // ...
}

## 📂 기타 참고

Dockerfile: 멀티스테이지 (Gradle → JDK Slim)

docker-compose.yml: MySQL + Spring Boot 서비스 정의

테스트용 DB는 H2, 운영/로컬은 MySQL 사용

## 📝 작성자

프로젝트 리더: @Yoonhojoon

작성일: 2025.05.07

