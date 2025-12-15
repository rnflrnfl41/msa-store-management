# MSA Store Management System

## 📋 프로젝트 개요

MSA Store Management System은 점포 관리, 고객 관리, 매출 관리, 포인트 관리, 지출 관리, 방문 관리 등을 위한 마이크로서비스 아키텍처 기반의 시스템입니다.

이 시스템은 Spring Cloud를 활용하여 확장 가능하고 유지보수가 용이한 마이크로서비스 아키텍처로 설계되었으며, BFF(Backend for Frontend) 패턴을 적용하여 프론트엔드와 백엔드 간의 통신을 최적화합니다.

## 🏗️ 아키텍처

### 전체 구조

```
msa-store-management/
├── common-lib/           # 공통 라이브러리
├── discovery-service/    # 서비스 디스커버리 (Eureka Server)
├── api-gateway/         # API 게이트웨이
├── bff-service/         # Backend for Frontend 서비스 (쿠키 관리)
├── auth-service/        # 인증 서비스 (외부 Auth Server 연동)
├── store-service/       # 점포 관리 서비스
├── customer-service/    # 고객 관리 서비스
├── sales-service/       # 매출 관리 서비스
├── benefit-service/     # 혜택 관리 서비스 (포인트, 쿠폰)
└── expense-service/     # 지출 관리 서비스
```

### 아키텍처 다이어그램

```
┌─────────────┐
│   Client    │
│ (Frontend)  │
└──────┬──────┘
       │
       │ HTTP (쿠키 포함)
       ▼
┌─────────────────┐
│   BFF Service   │ ◄─── 쿠키 관리, 세션 처리
│  (쿠키 관리)    │
└──────┬──────────┘
       │
       │ JWT Token
       ▼
┌─────────────────┐
│  API Gateway    │ ◄─── 라우팅, 인증/인가
└──────┬──────────┘
       │
       ├──────────┬──────────┬──────────┬──────────┬──────────┐
       ▼          ▼          ▼          ▼          ▼          ▼
┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐
│   Auth   │ │  Store   │ │ Customer │ │  Sales   │ │ Benefit  │ │ Expense  │
│ Service  │ │ Service  │ │ Service  │ │ Service  │ │ Service  │ │ Service  │
└────┬─────┘ └──────────┘ └──────────┘ └──────────┘ └──────────┘ └──────────┘
     │
     │ (외부 Auth Server 연동)
     ▼
┌─────────────────┐
│ External Auth   │
│     Server      │
└─────────────────┘

┌─────────────────┐
│     Redis       │ ◄─── Authorization 정보 저장
│  (인가 관리)    │
└─────────────────┘
```

### 서비스 포트 정보

| 서비스 | 포트 | 설명 |
|--------|------|------|
| Discovery Service | 8761 | Eureka 서버 |
| API Gateway | 8080 | 게이트웨이 (내부 서비스 진입점) |
| BFF Service | 8081 | Backend for Frontend (클라이언트 진입점, 쿠키 관리) |
| Auth Service | 8181 | 인증 서비스 (외부 Auth Server 연동) |
| Customer Service | 8082 | 고객 관리 서비스 |
| Expense Service | 8083 | 지출 관리 서비스 |
| Benefit Service | 8084 | 혜택 관리 서비스 |
| Sales Service | 8085 | 매출 관리 서비스 |
| Store Service | 8086 | 점포 관리 서비스 |

## 🚀 기술 스택

### Backend
- **Java**: 17
- **Spring Boot**: 3.3.5
- **Spring Cloud**: 2023.0.3
- **Spring Security**: 인증/인가
- **Spring Data JPA**: 데이터 접근
- **Spring Cloud Gateway**: API 게이트웨이
- **Netflix Eureka**: 서비스 디스커버리
- **MySQL**: 데이터베이스
- **Redis**: 인가 정보 저장 및 세션 관리
- **JWT**: 토큰 기반 인증
- **Lombok**: 보일러플레이트 코드 제거
- **ModelMapper**: 객체 매핑
- **SpringDoc OpenAPI**: API 문서화

### Build Tool
- **Gradle**: 멀티 프로젝트 빌드

## 📦 서비스별 상세 정보

### 1. Common Library (`common-lib`)
- **역할**: 모든 마이크로서비스에서 공통으로 사용되는 라이브러리
- **주요 구성**:
    - `CommonException`: 전역 예외 처리 클래스
    - `ServiceConstants`: 서비스명 상수 정의
    - `RoleConstants`: 역할 기반 접근 제어 상수
    - `HttpHeaderConstants`: HTTP 헤더 상수
    - 공통 DTO 및 유틸리티 클래스
    - JWT 유틸리티

### 2. Discovery Service (`discovery-service`)
- **포트**: 8761
- **역할**: 서비스 디스커버리 (Eureka Server)
- **기능**:
    - 마이크로서비스들의 등록 및 발견
    - 서비스 상태 모니터링
    - 로드 밸런싱을 위한 서비스 정보 제공
- **접속 URL**: `http://localhost:8761`

### 3. API Gateway (`api-gateway`)
- **포트**: 8080
- **역할**: 내부 마이크로서비스 간 통신의 게이트웨이
- **기술**: Spring Cloud Gateway + WebFlux
- **기능**:
    - 라우팅: `/api/{service}/**` 패턴으로 각 서비스로 라우팅
    - JWT 토큰 검증
    - Redis를 통한 인가 정보 확인
    - 로드 밸런싱: Eureka를 통한 동적 로드 밸런싱
- **라우팅 규칙**:
    - `/api/customer/**` → Customer Service
    - `/api/auth/**`, `/api/user/**` → Auth Service
    - `/api/store/**` → Store Service
    - `/api/expense/**` → Expense Service
    - `/api/benefit/**` → Benefit Service
    - `/api/sales/**` → Sales Service

### 4. BFF Service (`bff-service`)
- **포트**: 8081
- **역할**: Backend for Frontend - 프론트엔드와 백엔드 간의 중간 계층
- **주요 기능**:
    - **쿠키 관리**: Refresh Token을 HttpOnly 쿠키로 관리
    - **세션 처리**: 클라이언트 세션 정보 관리
    - **API 통합**: 여러 마이크로서비스의 응답을 통합하여 제공
    - **인증 처리**: 로그인/로그아웃 처리 및 쿠키 설정
    - **토큰 갱신**: Access Token 갱신 처리
- **특징**:
    - 클라이언트는 BFF Service를 통해서만 API에 접근
    - Refresh Token은 HttpOnly 쿠키로 저장하여 XSS 공격 방지
    - Access Token은 메모리 또는 로컬 스토리지에 저장
- **API 문서**: `http://localhost:8081/swagger-ui.html`

### 5. Auth Service (`auth-service`)
- **포트**: 8181
- **역할**: 인증 서비스 (외부 Auth Server와 연동)
- **주요 기능**:
    - 외부 Auth Server와의 연동
    - 사용자 인증 처리
    - JWT 토큰 발급/검증
    - Refresh Token 관리
    - Redis에 인가 정보 저장
- **외부 연동**:
    - 외부 Auth Server와 통신하여 사용자 인증
    - 인증 성공 시 JWT 토큰 발급
- **Redis 연동**:
    - 사용자 인가 정보를 Redis에 저장
    - 토큰과 권한 정보 매핑
- **데이터베이스**: MySQL
- **API 문서**: `http://localhost:8181/swagger-ui.html`

### 6. Store Service (`store-service`)
- **포트**: 8086
- **역할**: 점포 정보 관리
- **주요 기능**:
    - 점포 등록/수정/삭제
    - 점포 정보 조회
    - 점포별 설정 관리
    - 점포별 통계 조회
- **데이터베이스**: MySQL
- **API 문서**: `http://localhost:8086/swagger-ui.html`

### 7. Customer Service (`customer-service`)
- **포트**: 8082
- **역할**: 고객 정보 관리
- **주요 기능**:
    - 고객 등록/수정/삭제
    - 고객 정보 조회
    - 고객 이력 관리
    - 고객 검색 및 필터링
- **데이터베이스**: MySQL
- **API 문서**: `http://localhost:8082/swagger-ui.html`

### 8. Sales Service (`sales-service`)
- **포트**: 8085
- **역할**: 매출 정보 관리
- **주요 기능**:
    - 매출 데이터 등록 (방문 정보, 결제 정보)
    - 매출 통계 및 분석 (일별, 월별)
    - 매출 리포트 생성
    - 매출 차트 데이터 제공 (일별/월별)
    - 고객 서비스 이력 조회
    - 매출 삭제 (혜택 롤백 포함)
- **데이터베이스**: MySQL
- **서비스 간 통신**: Benefit Service와 통신하여 포인트/쿠폰 사용 처리
- **API 문서**: `http://localhost:8085/swagger-ui.html`

### 9. Benefit Service (`benefit-service`)
- **포트**: 8084
- **역할**: 고객 혜택 시스템 관리 (포인트, 쿠폰)
- **주요 기능**:
    - 포인트 적립/사용/조회
    - 쿠폰 발급/사용/조회
    - 혜택 통합 조회
    - 혜택 이력 관리
    - 혜택 사용 롤백 (트랜잭션 실패 시)
- **데이터베이스**: MySQL
- **API 문서**: `http://localhost:8084/swagger-ui.html`

### 10. Expense Service (`expense-service`)
- **포트**: 8083
- **역할**: 지출 정보 관리
- **주요 기능**:
    - 지출 데이터 등록
    - 지출 분류 및 분석
    - 지출 리포트 생성
    - 지출 통계 조회
- **데이터베이스**: MySQL
- **API 문서**: `http://localhost:8083/swagger-ui.html`

## 🔧 개발 환경 설정

### 필수 요구사항
- Java 17 이상
- Gradle 8.0 이상
- MySQL 8.0 이상
- Redis 6.0 이상

### 환경 변수 설정

프로젝트 실행 전 다음 환경 변수를 설정해야 합니다:

**Linux/Mac 환경**:
```bash
# 데이터베이스 설정
export DB_USERNAME=your_db_username
export DB_PASSWORD=your_db_password

# 각 서비스별 데이터베이스 URL
export AUTH_DB_URL=jdbc:mysql://localhost:3306/auth_db?useSSL=false&serverTimezone=Asia/Seoul
export CUSTOMER_DB_URL=jdbc:mysql://localhost:3306/customer_db?useSSL=false&serverTimezone=Asia/Seoul
export STORE_DB_URL=jdbc:mysql://localhost:3306/store_db?useSSL=false&serverTimezone=Asia/Seoul
export SALES_DB_URL=jdbc:mysql://localhost:3306/sales_db?useSSL=false&serverTimezone=Asia/Seoul
export BENEFIT_DB_URL=jdbc:mysql://localhost:3306/benefit_db?useSSL=false&serverTimezone=Asia/Seoul
export EXPENSE_DB_URL=jdbc:mysql://localhost:3306/expense_db?useSSL=false&serverTimezone=Asia/Seoul

# Redis 설정
export REDIS_HOST=localhost
export REDIS_PORT=6379
export REDIS_PASSWORD=

# JWT 시크릿 키
export JWT_SECRET=your_jwt_secret_key_here

# 내부 서비스 통신용 토큰
export INTERNAL_TOKEN=your_internal_token_here

# 외부 Auth Server 설정
export EXTERNAL_AUTH_SERVER_URL=http://your-auth-server-url
export EXTERNAL_AUTH_API_KEY=your_api_key
```

**Windows 환경 (CMD)**:
```cmd
set DB_USERNAME=your_db_username
set DB_PASSWORD=your_db_password
set REDIS_HOST=localhost
set REDIS_PORT=6379
set JWT_SECRET=your_jwt_secret_key_here
set INTERNAL_TOKEN=your_internal_token_here
set EXTERNAL_AUTH_SERVER_URL=http://your-auth-server-url
set EXTERNAL_AUTH_API_KEY=your_api_key
```

**Windows 환경 (PowerShell)**:
```powershell
$env:DB_USERNAME="your_db_username"
$env:DB_PASSWORD="your_db_password"
$env:REDIS_HOST="localhost"
$env:REDIS_PORT="6379"
$env:JWT_SECRET="your_jwt_secret_key_here"
$env:INTERNAL_TOKEN="your_internal_token_here"
$env:EXTERNAL_AUTH_SERVER_URL="http://your-auth-server-url"
$env:EXTERNAL_AUTH_API_KEY="your_api_key"
```

### 데이터베이스 설정

각 서비스별로 독립된 데이터베이스를 생성해야 합니다:

```sql
CREATE DATABASE auth_db;
CREATE DATABASE customer_db;
CREATE DATABASE store_db;
CREATE DATABASE sales_db;
CREATE DATABASE benefit_db;
CREATE DATABASE expense_db;
```

데이터베이스 스키마는 `migration-scripts/` 디렉토리에 있는 마이그레이션 스크립트를 참고하세요.

### Redis 설정

Redis를 설치하고 실행해야 합니다:

**Docker를 사용한 Redis 실행**:
```bash
docker run -d -p 6379:6379 --name redis redis:latest
```

**Redis 설정 확인**:
```bash
redis-cli ping
# 응답: PONG
```

Redis는 다음 용도로 사용됩니다:
- 사용자 인가 정보 저장 (토큰과 권한 매핑)
- 세션 정보 관리
- 토큰 블랙리스트 관리

### 프로젝트 빌드

```bash
# 전체 프로젝트 빌드
./gradlew build

# 특정 서비스만 빌드
./gradlew :bff-service:build
./gradlew :auth-service:build
./gradlew :store-service:build
./gradlew :sales-service:build
```

**Windows 환경**:
```cmd
gradlew.bat build
```

### 서비스 실행 순서

서비스는 다음 순서로 실행해야 합니다:

1. **Redis** 실행
   ```bash
   # Docker 사용 시
   docker start redis
   
   # 또는 로컬 Redis 실행
   redis-server
   ```

2. **Discovery Service** 실행 (포트: 8761)
   ```bash
   ./gradlew :discovery-service:bootRun
   ```
   또는 IDE에서 `DiscoveryServiceApplication` 실행

3. **API Gateway** 실행 (포트: 8080)
   ```bash
   ./gradlew :api-gateway:bootRun
   ```
   또는 IDE에서 `ApiGatewayApplication` 실행

4. **Auth Service** 실행 (포트: 8181)
   ```bash
   ./gradlew :auth-service:bootRun
   ```
   또는 IDE에서 `AuthServiceApplication` 실행

5. **BFF Service** 실행 (포트: 8081)
   ```bash
   ./gradlew :bff-service:bootRun
   ```
   또는 IDE에서 `BffServiceApplication` 실행

6. **각 비즈니스 서비스** 실행
   ```bash
   # Customer Service
   ./gradlew :customer-service:bootRun
   
   # Store Service
   ./gradlew :store-service:bootRun
   
   # Sales Service
   ./gradlew :sales-service:bootRun
   
   # Benefit Service
   ./gradlew :benefit-service:bootRun
   
   # Expense Service
   ./gradlew :expense-service:bootRun
   ```

**주의사항**:
- Redis는 반드시 먼저 실행되어야 합니다.
- Discovery Service는 Redis 이후에 실행되어야 합니다.
- API Gateway와 Auth Service는 Discovery Service 이후에 실행하는 것을 권장합니다.
- BFF Service는 Auth Service 이후에 실행하는 것을 권장합니다.
- 비즈니스 서비스들은 순서에 관계없이 실행 가능하지만, 서비스 간 의존성이 있는 경우 순서를 고려해야 합니다.

## 📚 API 문서

각 서비스는 SpringDoc OpenAPI를 통해 API 문서를 제공합니다:

- **BFF Service**: `http://localhost:8081/swagger-ui.html` (클라이언트 진입점)
- **API Gateway**: `http://localhost:8080/swagger-ui.html`
- **Auth Service**: `http://localhost:8181/swagger-ui.html`
- **Customer Service**: `http://localhost:8082/swagger-ui.html`
- **Expense Service**: `http://localhost:8083/swagger-ui.html`
- **Benefit Service**: `http://localhost:8084/swagger-ui.html`
- **Sales Service**: `http://localhost:8085/swagger-ui.html`
- **Store Service**: `http://localhost:8086/swagger-ui.html`

### API 엔드포인트 예시

모든 클라이언트 요청은 BFF Service를 통해 접근합니다:

```
# 인증 (BFF Service를 통해)
POST http://localhost:8081/api/auth/login
  → 쿠키에 Refresh Token 저장
  → 응답에 Access Token 포함

POST http://localhost:8081/api/auth/logout
  → 쿠키에서 Refresh Token 제거

POST http://localhost:8081/api/auth/refresh
  → 쿠키의 Refresh Token으로 Access Token 갱신

# 고객 관리
GET  http://localhost:8081/api/customer/{id}
POST http://localhost:8081/api/customer

# 점포 관리
GET  http://localhost:8081/api/store/{id}
POST http://localhost:8081/api/store

# 매출 관리
POST http://localhost:8081/api/sales
GET  http://localhost:8081/api/sales/summary?date=2024-01-01

# 혜택 관리
GET  http://localhost:8081/api/benefit/point/{customerId}
POST http://localhost:8081/api/benefit/point/use

# 지출 관리
POST http://localhost:8081/api/expense
GET  http://localhost:8081/api/expense
```

## 🛡️ 보안

### 인증/인가 아키텍처

#### 인증 흐름
1. 클라이언트가 BFF Service에 로그인 요청
2. BFF Service가 Auth Service를 통해 외부 Auth Server와 통신
3. 인증 성공 시:
    - Access Token: 응답 본문에 포함 (클라이언트가 메모리/로컬 스토리지에 저장)
    - Refresh Token: HttpOnly 쿠키로 설정 (XSS 공격 방지)
    - 인가 정보: Redis에 저장 (토큰과 권한 매핑)

#### 인가 흐름
1. 클라이언트가 BFF Service에 요청 (Access Token 포함)
2. BFF Service가 API Gateway로 요청 전달 (JWT 토큰 포함)
3. API Gateway에서:
    - JWT 토큰 검증
    - Redis에서 인가 정보 확인
    - 권한 확인 후 요청 라우팅

#### Redis를 통한 인가 관리
- **토큰-권한 매핑**: `token:{accessToken}` → 권한 정보
- **사용자 세션**: `session:{userId}` → 세션 정보
- **토큰 블랙리스트**: `blacklist:{token}` → 만료된 토큰
- **TTL 설정**: 토큰 만료 시간에 맞춰 자동 삭제

### 보안 설정
- **JWT 기반 인증**: 토큰 기반의 무상태 인증
- **Redis 기반 인가**: 빠른 인가 정보 조회 및 관리
- **HttpOnly 쿠키**: Refresh Token을 쿠키로 저장하여 XSS 공격 방지
- **SameSite 쿠키**: CSRF 공격 방지
- **Spring Security**: 역할 기반 접근 제어 (RBAC)
- **API Gateway**: 중앙화된 보안 정책 적용
- **내부 서비스 통신**: INTERNAL_TOKEN을 통한 서비스 간 인증

### 쿠키 설정 (BFF Service)
```java
// Refresh Token 쿠키 설정
ResponseCookie.from("refreshToken", refreshToken)
    .httpOnly(true)        // JavaScript 접근 불가
    .secure(true)          // HTTPS 환경에서만 전송
    .sameSite("Strict")    // CSRF 방지
    .path("/")             // 전체 경로에서 사용
    .maxAge(Duration.ofDays(7))  // 7일 유효
    .build();
```

## 🔄 서비스 간 통신

### 통신 방식
- **클라이언트 → BFF Service**: HTTP (쿠키 포함)
- **BFF Service → API Gateway**: HTTP (JWT 토큰 포함)
- **API Gateway → 마이크로서비스**: HTTP (로드 밸런싱)
- **서비스 간 통신**: REST API를 통한 HTTP 통신
- **서비스 디스커버리**: Eureka를 통한 동적 서비스 발견

### 서비스 간 의존성
- **BFF Service** → **Auth Service**: 인증 처리
- **BFF Service** → **API Gateway**: 비즈니스 로직 요청
- **Auth Service** → **외부 Auth Server**: 사용자 인증
- **Auth Service** → **Redis**: 인가 정보 저장/조회
- **Sales Service** → **Benefit Service**: 포인트/쿠폰 사용 처리
- **Store Service** → **Auth Service**: 사용자 정보 조회
- **Customer Service** → **Benefit Service**: 고객 혜택 정보 조회

### 트랜잭션 관리
- 각 서비스는 독립적인 데이터베이스를 사용
- 분산 트랜잭션은 Saga 패턴을 통해 처리
- 실패 시 롤백 메커니즘 구현 (예: Benefit Service의 롤백 API)
- Redis 트랜잭션은 단일 명령어로 처리하여 일관성 보장

## 📁 프로젝트 구조

각 서비스는 다음과 같은 구조를 따릅니다:

```
[service-name]/
├── src/
│   ├── main/
│   │   ├── java/com/example/[service]/
│   │   │   ├── controller/       # REST API 컨트롤러
│   │   │   ├── service/           # 비즈니스 로직
│   │   │   ├── repository/       # 데이터 접근 계층
│   │   │   ├── entity/            # JPA 엔티티
│   │   │   ├── dto/               # 데이터 전송 객체
│   │   │   ├── config/            # 설정 클래스
│   │   │   ├── exception/         # 예외 처리
│   │   │   ├── client/            # Feign Client (서비스 간 통신)
│   │   │   └── redis/             # Redis 관련 클래스 (Auth Service)
│   │   └── resources/
│   │       └── application.yml    # 설정 파일
│   └── test/                      # 테스트 코드
├── build.gradle                   # 빌드 설정
└── settings.gradle                # 프로젝트 설정
```

### BFF Service 구조 예시
```
bff-service/
├── src/main/java/com/example/bff/
│   ├── controller/
│   │   └── BffController.java    # 클라이언트 요청 처리
│   ├── service/
│   │   ├── AuthService.java      # 인증 처리
│   │   └── ApiProxyService.java   # API Gateway 프록시
│   ├── config/
│   │   ├── CookieConfig.java      # 쿠키 설정
│   │   └── WebClientConfig.java   # WebClient 설정
│   └── dto/
│       └── AuthDto.java           # 인증 관련 DTO
```

### Auth Service Redis 구조
```
auth-service/
├── src/main/java/com/example/authservice/
│   ├── redis/
│   │   ├── AuthorizationRepository.java  # Redis 인가 정보 저장/조회
│   │   └── RedisConfig.java                # Redis 설정
│   └── service/
│       └── AuthService.java                # Redis 연동 로직
```

## 🧪 테스트

```bash
# 전체 테스트 실행
./gradlew test

# 특정 서비스 테스트
./gradlew :bff-service:test
./gradlew :auth-service:test
./gradlew :sales-service:test
```

## 🚀 배포

### 개발 환경
- 각 서비스를 개별적으로 실행하여 개발
- 로컬 환경에서 독립적인 개발 및 테스트
- IDE에서 직접 실행 가능

### 프로덕션 환경
- Docker 컨테이너화 지원 (준비 중)
- Kubernetes 오케스트레이션 준비
- Redis 클러스터 구성
- 로드 밸런서 및 모니터링 시스템 연동

## 🐛 트러블슈팅

### 일반적인 문제

1. **Redis 연결 실패**
    - Redis가 실행 중인지 확인: `redis-cli ping`
    - `REDIS_HOST`, `REDIS_PORT` 환경 변수 확인
    - 방화벽 설정 확인

2. **쿠키가 설정되지 않음**
    - BFF Service가 실행 중인지 확인
    - 브라우저 개발자 도구에서 쿠키 설정 확인
    - CORS 설정 확인 (쿠키 전송을 위해 `allowCredentials: true` 필요)

3. **인가 정보가 Redis에 저장되지 않음**
    - Auth Service의 Redis 연결 확인
    - Redis 로그 확인
    - 토큰 생성 로직 확인

4. **서비스가 Eureka에 등록되지 않음**
    - Discovery Service가 실행 중인지 확인
    - `application.yml`의 Eureka 설정 확인
    - 네트워크 연결 확인

5. **데이터베이스 연결 실패**
    - 환경 변수가 올바르게 설정되었는지 확인
    - 데이터베이스가 실행 중인지 확인
    - 데이터베이스 URL 및 인증 정보 확인

6. **JWT 토큰 검증 실패**
    - `JWT_SECRET` 환경 변수 확인
    - 모든 서비스에서 동일한 `JWT_SECRET` 사용 확인
    - 토큰 만료 시간 확인

7. **외부 Auth Server 연동 실패**
    - `EXTERNAL_AUTH_SERVER_URL` 환경 변수 확인
    - 네트워크 연결 확인
    - API 키 확인

## 🤝 기여 가이드

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📄 라이선스

이 프로젝트는 MIT 라이선스 하에 배포됩니다.

## 📞 문의

프로젝트에 대한 문의사항이 있으시면 이슈를 생성해 주세요.

---

**참고**: 이 프로젝트는 현재 개발 중이며, 일부 기능이 완성되지 않았을 수 있습니다.
