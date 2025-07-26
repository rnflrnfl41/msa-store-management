# MSA 서비스 실행 가이드 (README)

Spring Cloud 기반 MSA 프로젝트입니다. 각 서비스는 다음과 같이 구성되어 있습니다:

- discovery-service (Eureka)
- customer-service
- api-gateway

---

## 1. 로컬 실행 (개발 단계)

### 🔹 서비스별 부팅 (각각 다른 터미널에서 실행)

```bash
./gradlew :discovery-service:bootRun
./gradlew :customer-service:bootRun
./gradlew :api-gateway:bootRun
```

🔹 비즈니스 로직 테스트
```bash
curl http://localhost:8080/api/customers
```
8080 포트는 api-gateway에 해당하며, 내부적으로 customer-service로 요청이 전달됩니다.

🔹 변경사항 적용 (부분 재시작)
```bash
# 실행 중인 customer-service 터미널에서 Ctrl+C로 중지 후
./gradlew :customer-service:bootRun
```

## 2. Docker 기반 실행 (예정)

향후 다음과 같은 방식으로 Docker로 전환 예정입니다:

각 서비스에 Dockerfile 추가

docker-compose.yml로 전체 서비스 컨테이너 구성 및 실행

서비스별 재시작 가능

예시 (구현 예정):

```bash
# 전체 서비스 빌드 및 실행
docker-compose up --build

# 특정 서비스만 재시작
docker-compose restart customer-service
```

## 3. 프로젝트 구조 예시
```bash
├── discovery-service/
├── customer-service/
├── api-gateway/
├── docker-compose.yml   # (예정)
└── README.md
```

## 4. 개발 환경 요구사항
Java 17 이상

Gradle (권장: Wrapper 사용 ./gradlew)

Spring Boot / Spring Cloud