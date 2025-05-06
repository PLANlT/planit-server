# 🛠️ Planit - Spring Boot 백엔드 (Docker 기반 실행환경 통일)

Planit은 Spring Boot 기반의 백엔드 서비스로, 개발/운영/테스트 환경을 Docker와 Spring Profile 분리를 통해 일관성 있게 관리합니다.

---

## 📦 기술 스택

- Java 17
- Spring Boot
- JPA (H2 / MySQL)
- Docker / Docker Compose

---

## 📁 환경 구성

| 환경 | 설명 | DB |
|------|------|----|
| dev  | 개발용, 빠른 테스트용 | H2 (In-Memory) |
| prod | 운영 배포용 | MySQL |
| test | 테스트 자동화/TDD용 | H2 (create-drop) |

---

## 🚀 실행 방법

### 1. `.env` 파일 생성

아래 예시를 참고하여 `.env.dev`, `.env.prod`, `.env.test` 파일을 **루트 디렉토리에 생성**합니다.

> ⚠️ **보안 주의:** 실제 계정/비밀번호가 포함된 `.env.prod`는 Git에 절대 커밋하지 말고, `.gitignore`에 반드시 포함시키세요.

<details>
<summary>📄 .env.dev (예시)</summary>

```
SPRING_PROFILES_ACTIVE=dev
```

</details>

<details>
<summary>📄 .env.prod (예시 - 실제 비밀번호 절대 커밋 금지)</summary>

```
SPRING_PROFILES_ACTIVE=prod

SPRING_DATASOURCE_URL=jdbc:mysql://<prod-db-host>:3306/planit
SPRING_DATASOURCE_USERNAME=<prod_user>
SPRING_DATASOURCE_PASSWORD=<prod_password>

MYSQL_DATABASE=planit
MYSQL_ROOT_PASSWORD=<mysql_root_pw>
```

</details>

<details>
<summary>📄 .env.test (예시)</summary>

```
SPRING_PROFILES_ACTIVE=test
```

</details>

---

### 2. Docker 실행

```bash
# 개발 환경 (H2 기반)
docker-compose --env-file .env.dev up -d

# 운영 환경 (MySQL 기반)
docker-compose --env-file .env.prod up -d

# 테스트 환경 (TDD용 H2 + create-drop)
docker-compose --env-file .env.test up -d
```

### 컨테이너 종료

```bash
docker-compose down
```

---

## 🧪 테스트 실행 (로컬 JUnit 기준)

### 방법 1: VM 옵션 지정

```
-Dspring.profiles.active=test
```

### 방법 2: 테스트 클래스에 직접 지정

```java
@ActiveProfiles("test")
@SpringBootTest
class SomeServiceTest {
 
}
```

### 방법 3: Gradle 설정에 추가

`build.gradle`에 다음 추가:

```groovy
test {
    useJUnitPlatform()
    systemProperty "spring.profiles.active", "test"
}
```

---


## 📌 Git 설정 주의사항

`.gitignore`에 다음 항목을 추가해 주세요:

```
.env.dev
.env.prod
.env.test
```

---

