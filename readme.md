# MSA Store Management System

## 📋 프로젝트 개요

MSA Store Management System은 점포 관리, 고객 관리, 매출 관리, 포인트 관리, 지출 관리, 방문 관리 등을 위한 마이크로서비스 아키텍처 기반의 시스템입니다.

## 🏗️ 아키텍처

### 전체 구조
```
msa-store-management/
├── common-lib/           # 공통 라이브러리
├── discovery-service/    # 서비스 디스커버리 (Eureka Server)
├── api-gateway/         # API 게이트웨이
├── auth-service/        # 인증/인가 서비스
├── store-service/       # 점포 관리 서비스
├── customer-service/    # 고객 관리 서비스
├── sales-service/       # 매출 관리 서비스
├── benefit-service/     # 혜택 관리 서비스 (포인트, 쿠폰)
├── expense-service/     # 지출 관리 서비스
└── visit-service/       # 방문 관리 서비스
```

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

### 2. Discovery Service (`discovery-service`)
- **역할**: 서비스 디스커버리 (Eureka Server)
- **포트**: 기본 8761
- **기능**: 마이크로서비스들의 등록 및 발견

### 3. API Gateway (`api-gateway`)
- **역할**: 모든 클라이언트 요청의 진입점
- **기술**: Spring Cloud Gateway + WebFlux
- **기능**: 
  - 라우팅
  - 인증/인가
  - 로드 밸런싱
  - 요청/응답 변환

### 4. Auth Service (`auth-service`)
- **역할**: 사용자 인증 및 권한 관리
- **주요 기능**:
  - 사용자 등록/로그인
  - JWT 토큰 발급/검증
  - 역할 기반 접근 제어
  - 보안 설정
- **데이터베이스**: MySQL
- **API 문서**: SpringDoc OpenAPI

### 5. Store Service (`store-service`)
- **역할**: 점포 정보 관리
- **주요 기능**:
  - 점포 등록/수정/삭제
  - 점포 정보 조회
  - 점포별 설정 관리
- **데이터베이스**: MySQL
- **API 문서**: SpringDoc OpenAPI

### 6. Customer Service (`customer-service`)
- **역할**: 고객 정보 관리
- **주요 기능**:
  - 고객 등록/수정/삭제
  - 고객 정보 조회
  - 고객 이력 관리
- **데이터베이스**: MySQL

### 7. Sales Service (`sales-service`)
- **역할**: 매출 정보 관리
- **주요 기능**:
  - 매출 데이터 등록
  - 매출 통계 및 분석
  - 매출 리포트 생성
- **데이터베이스**: MySQL

### 8. Benefit Service (`benefit-service`)
- **역할**: 고객 혜택 시스템 관리 (포인트, 쿠폰)
- **주요 기능**:
  - 포인트 적립/사용
  - 쿠폰 발급/사용
  - 혜택 통합 조회
  - 혜택 이력 관리
- **데이터베이스**: MySQL

### 9. Expense Service (`expense-service`)
- **역할**: 지출 정보 관리
- **주요 기능**:
  - 지출 데이터 등록
  - 지출 분류 및 분석
  - 지출 리포트 생성
- **데이터베이스**: MySQL

### 10. Visit Service (`visit-service`)
- **역할**: 고객 방문 정보 관리
- **주요 기능**:
  - 방문 기록 등록
  - 방문 통계 분석
  - 방문 패턴 분석
- **데이터베이스**: MySQL

## 🔧 개발 환경 설정

### 필수 요구사항
- Java 17 이상
- Gradle 8.0 이상
- MySQL 8.0 이상

### 프로젝트 빌드
```bash
# 전체 프로젝트 빌드
./gradlew build

# 특정 서비스만 빌드
./gradlew :auth-service:build
./gradlew :store-service:build
```

### 서비스 실행 순서
1. **Discovery Service** 실행 (포트: 8761)
2. **API Gateway** 실행
3. **각 비즈니스 서비스** 실행

## 📚 API 문서

각 서비스는 SpringDoc OpenAPI를 통해 API 문서를 제공합니다:
- Auth Service: `http://localhost:8080/swagger-ui.html`
- Store Service: `http://localhost:8081/swagger-ui.html`
- 기타 서비스들도 동일한 패턴으로 접근 가능

## 🛡️ 보안

- **JWT 기반 인증**: 토큰 기반의 무상태 인증
- **Spring Security**: 역할 기반 접근 제어
- **API Gateway**: 중앙화된 보안 정책 적용

## 🔄 서비스 간 통신

- **동기 통신**: REST API를 통한 HTTP 통신
- **서비스 디스커버리**: Eureka를 통한 동적 서비스 발견
- **로드 밸런싱**: API Gateway를 통한 요청 분산

## 📁 프로젝트 구조

```
src/main/java/com/example/
├── [service-name]/
│   ├── controller/       # REST API 컨트롤러
│   ├── service/         # 비즈니스 로직
│   ├── repository/      # 데이터 접근 계층
│   ├── entity/          # JPA 엔티티
│   ├── dto/             # 데이터 전송 객체
│   ├── config/          # 설정 클래스
│   └── exception/       # 예외 처리
```

## 🚀 배포

### 개발 환경
- 각 서비스를 개별적으로 실행하여 개발
- 로컬 환경에서 독립적인 개발 및 테스트

### 프로덕션 환경
- Docker 컨테이너화 지원
- Kubernetes 오케스트레이션 준비
- 로드 밸런서 및 모니터링 시스템 연동

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
