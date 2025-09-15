# Demo API

상품 관리 및 장바구니 계산 기능을 제공하는 Spring Boot 기반 REST API입니다.

## 📋 기능

### 상품 관리
- 상품 CRUD 작업 (생성, 조회, 수정, 삭제)
- 페이지네이션 및 정렬 지원
- 상품 검색 및 필터링

### 장바구니 관리
- 장바구니 총액 계산
- 할인 정책 자동 적용
- 상품 검증 (재고 확인, 상품 존재 여부)
- 다양한 할인 타입 지원 (비율 할인, 고정 금액 할인)

## 🚀 시작하기

### 요구사항
- Java 17+
- Spring Boot 3.x
- Gradle
- H2 Database (개발용)

### 설치 및 실행

```bash
# 프로젝트 클론
git clone <repository-url>
cd demo

# 의존성 설치 및 빌드
./gradlew build

# 애플리케이션 실행
./gradlew bootRun
```

애플리케이션이 실행되면 `http://localhost:8080`에서 API에 접근할 수 있습니다.

## 📚 API 문서

### OpenAPI 스펙
이 프로젝트는 OpenAPI 3.0 스펙을 지원합니다.

- **YAML 형식**: `openapi.yaml`
- **JSON 형식**: `openapi.json`

### API 문서 확인 방법

#### 1. Swagger UI 사용
```bash
# 애플리케이션 실행 후 브라우저에서 접속
http://localhost:8080/swagger-ui.html
```

#### 2. OpenAPI 파일 직접 사용
```bash
# YAML 파일을 Swagger Editor에서 열기
https://editor.swagger.io/

# 또는 로컬에서 파일 열기
open openapi.yaml
```

#### 3. Postman에서 Import
1. Postman 실행
2. Import 버튼 클릭
3. `openapi.json` 또는 `openapi.yaml` 파일 선택
4. API 컬렉션 자동 생성

### 주요 엔드포인트

#### 상품 관리 API (`/api/products`)
- `GET /api/products` - 전체 상품 조회
- `GET /api/products/{id}` - 상품 상세 조회
- `POST /api/products` - 상품 생성
- `PUT /api/products/{id}` - 상품 수정
- `DELETE /api/products/{id}` - 상품 삭제

#### 장바구니 API (`/api/cart`)
- `POST /api/cart/calculate` - 장바구니 총액 계산 (검증 포함)
- `POST /api/cart/calculate-without-validation` - 장바구니 총액 계산 (검증 없음)
- `POST /api/cart/validate` - 장바구니 검증

## 🧪 테스트

### 단위 테스트 실행
```bash
# 전체 테스트 실행
./gradlew test

# 특정 테스트 클래스 실행
./gradlew test --tests CartServiceTest

# 테스트 결과 확인
find build/reports/tests -name "*.html" | head -1 | xargs cat
```

### API 테스트 예시

#### 상품 생성
```bash
curl -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -d '{
    "name": "테스트 상품",
    "price": 10000,
    "stock": 100,
    "category": "전자제품"
  }'
```

#### 장바구니 계산
```bash
curl -X POST http://localhost:8080/api/cart/calculate \
  -H "Content-Type: application/json" \
  -d '{
    "items": [
      {
        "productId": 1,
        "quantity": 2
      }
    ]
  }'
```

## 🗄️ 데이터베이스

### H2 인메모리 데이터베이스
- 개발 환경에서 H2 인메모리 데이터베이스 사용
- 애플리케이션 시작 시 샘플 데이터 자동 생성
- H2 콘솔 접속: `http://localhost:8080/h2-console`

### 데이터베이스 설정
```properties
# application.properties
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.h2.console.enabled=true
```

## 📊 데이터 모델

### Product (상품)
- 상품 기본 정보 (이름, 설명, 가격, 재고)
- 카테고리, 브랜드, SKU 정보
- 활성 상태 및 생성/수정 시간

### DiscountPolicy (할인 정책)
- 다양한 할인 타입 지원 (비율, 고정 금액)
- 다양한 할인 대상 지원 (상품, 카테고리, 브랜드, 수량 등)
- 할인 조건 설정 (최소 주문 금액, 수량 범위 등)

## 🔧 개발 가이드

### 프로젝트 구조
```
src/
├── main/
│   ├── kotlin/
│   │   └── com/example/demo/
│   │       ├── controller/     # REST 컨트롤러
│   │       ├── service/        # 비즈니스 로직
│   │       ├── repository/     # 데이터 접근 계층
│   │       ├── entity/         # JPA 엔티티
│   │       ├── dto/            # 데이터 전송 객체
│   │       └── config/         # 설정 클래스
│   └── resources/
│       └── application.properties
└── test/
    └── kotlin/
        └── com/example/demo/
            └── service/        # 단위 테스트
```

### 코딩 컨벤션
- Kotlin 언어 사용
- MockK를 이용한 단위 테스트
- TDD 방법론 적용
- 한국어 주석 및 문서화

## 📝 라이선스

이 프로젝트는 MIT 라이선스 하에 배포됩니다.

## 🤝 기여하기

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📞 지원

문제가 발생하거나 질문이 있으시면 이슈를 생성해 주세요.

---

**API 문서**: `openapi.yaml` 또는 `openapi.json` 파일을 참조하세요.
