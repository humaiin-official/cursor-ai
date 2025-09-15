# API 문서

## 개요
이 프로젝트는 상품 관리 및 장바구니 계산 기능을 제공하는 Spring Boot 기반 REST API입니다.

## 기본 정보
- **Base URL**: `http://localhost:8080`
- **Content-Type**: `application/json`
- **CORS**: 모든 도메인 허용 (`*`)

---

## 상품 관리 API (`/api/products`)

### 1. 전체 상품 조회
**GET** `/api/products`

상품 목록을 페이지네이션과 정렬 기능을 통해 조회합니다.

#### Query Parameters
| 파라미터 | 타입 | 기본값 | 설명 |
|---------|------|--------|------|
| `page` | Integer | 0 | 페이지 번호 (0부터 시작) |
| `size` | Integer | 20 | 페이지당 상품 수 |
| `sortBy` | String | "createdAt" | 정렬 기준 필드 |
| `direction` | String | "desc" | 정렬 방향 (asc/desc) |

#### Response
```json
{
  "products": [
    {
      "id": 1,
      "name": "상품명",
      "description": "상품 설명",
      "price": 10000.00,
      "stock": 100,
      "category": "카테고리",
      "brand": "브랜드",
      "sku": "SKU123",
      "isActive": true,
      "createdAt": "2024-01-01T00:00:00",
      "updatedAt": "2024-01-01T00:00:00"
    }
  ],
  "totalCount": 1
}
```

#### HTTP Status Codes
- `200 OK`: 성공
- `500 Internal Server Error`: 서버 오류

---

### 2. 상품 상세 조회
**GET** `/api/products/{id}`

특정 상품의 상세 정보를 조회합니다.

#### Path Parameters
| 파라미터 | 타입 | 설명 |
|---------|------|------|
| `id` | Long | 상품 ID |

#### Response
```json
{
  "id": 1,
  "name": "상품명",
  "description": "상품 설명",
  "price": 10000.00,
  "stock": 100,
  "category": "카테고리",
  "brand": "브랜드",
  "sku": "SKU123",
  "isActive": true,
  "createdAt": "2024-01-01T00:00:00",
  "updatedAt": "2024-01-01T00:00:00"
}
```

#### HTTP Status Codes
- `200 OK`: 성공
- `404 Not Found`: 상품을 찾을 수 없음
- `500 Internal Server Error`: 서버 오류

---

### 3. 상품 생성
**POST** `/api/products`

새로운 상품을 생성합니다.

#### Request Body
```json
{
  "name": "상품명",
  "description": "상품 설명",
  "price": 10000.00,
  "stock": 100,
  "category": "카테고리",
  "brand": "브랜드",
  "sku": "SKU123",
  "isActive": true
}
```

#### Response
```json
{
  "id": 1,
  "name": "상품명",
  "description": "상품 설명",
  "price": 10000.00,
  "stock": 100,
  "category": "카테고리",
  "brand": "브랜드",
  "sku": "SKU123",
  "isActive": true,
  "createdAt": "2024-01-01T00:00:00",
  "updatedAt": "2024-01-01T00:00:00"
}
```

#### HTTP Status Codes
- `201 Created`: 성공적으로 생성됨
- `500 Internal Server Error`: 서버 오류

---

### 4. 상품 수정
**PUT** `/api/products/{id}`

기존 상품의 정보를 수정합니다.

#### Path Parameters
| 파라미터 | 타입 | 설명 |
|---------|------|------|
| `id` | Long | 상품 ID |

#### Request Body
```json
{
  "name": "수정된 상품명",
  "description": "수정된 상품 설명",
  "price": 15000.00,
  "stock": 50,
  "category": "수정된 카테고리",
  "brand": "수정된 브랜드",
  "sku": "SKU456",
  "isActive": true
}
```

#### Response
```json
{
  "id": 1,
  "name": "수정된 상품명",
  "description": "수정된 상품 설명",
  "price": 15000.00,
  "stock": 50,
  "category": "수정된 카테고리",
  "brand": "수정된 브랜드",
  "sku": "SKU456",
  "isActive": true,
  "createdAt": "2024-01-01T00:00:00",
  "updatedAt": "2024-01-01T12:00:00"
}
```

#### HTTP Status Codes
- `200 OK`: 성공적으로 수정됨
- `404 Not Found`: 상품을 찾을 수 없음
- `500 Internal Server Error`: 서버 오류

---

### 5. 상품 삭제
**DELETE** `/api/products/{id}`

특정 상품을 삭제합니다.

#### Path Parameters
| 파라미터 | 타입 | 설명 |
|---------|------|------|
| `id` | Long | 상품 ID |

#### Response
- 응답 본문 없음

#### HTTP Status Codes
- `204 No Content`: 성공적으로 삭제됨
- `404 Not Found`: 상품을 찾을 수 없음
- `500 Internal Server Error`: 서버 오류

---

## 장바구니 API (`/api/cart`)

### 1. 장바구니 총액 계산 (검증 포함)
**POST** `/api/cart/calculate`

장바구니 아이템들의 총액을 계산하며, 상품 존재 여부와 재고를 검증합니다.

#### Request Body
```json
{
  "items": [
    {
      "productId": 1,
      "quantity": 2
    },
    {
      "productId": 2,
      "quantity": 1
    }
  ]
}
```

#### Response
```json
{
  "items": [
    {
      "productId": 1,
      "productName": "상품1",
      "price": 10000.00,
      "quantity": 2,
      "totalPrice": 20000.00,
      "discountAmount": 2000.00,
      "finalPrice": 18000.00
    },
    {
      "productId": 2,
      "productName": "상품2",
      "price": 15000.00,
      "quantity": 1,
      "totalPrice": 15000.00,
      "discountAmount": 0.00,
      "finalPrice": 15000.00
    }
  ],
  "totalAmount": 35000.00,
  "totalItemCount": 3,
  "totalDiscountAmount": 2000.00,
  "finalTotalAmount": 33000.00
}
```

#### HTTP Status Codes
- `200 OK`: 성공
- `400 Bad Request`: 검증 실패 (상품 없음, 재고 부족 등)
- `500 Internal Server Error`: 서버 오류

---

### 2. 장바구니 총액 계산 (검증 없음)
**POST** `/api/cart/calculate-without-validation`

장바구니 아이템들의 총액을 계산하되, 검증 없이 계산합니다.

#### Request Body
```json
{
  "items": [
    {
      "productId": 1,
      "quantity": 2
    },
    {
      "productId": 999,
      "quantity": 1
    }
  ]
}
```

#### Response
```json
{
  "items": [
    {
      "productId": 1,
      "productName": "상품1",
      "price": 10000.00,
      "quantity": 2,
      "totalPrice": 20000.00,
      "discountAmount": 2000.00,
      "finalPrice": 18000.00
    }
  ],
  "totalAmount": 20000.00,
  "totalItemCount": 2,
  "totalDiscountAmount": 2000.00,
  "finalTotalAmount": 18000.00
}
```

#### HTTP Status Codes
- `200 OK`: 성공
- `500 Internal Server Error`: 서버 오류

---

### 3. 장바구니 검증
**POST** `/api/cart/validate`

장바구니 아이템들의 유효성을 검증합니다.

#### Request Body
```json
{
  "items": [
    {
      "productId": 1,
      "quantity": 10
    },
    {
      "productId": 999,
      "quantity": 1
    }
  ]
}
```

#### Response
```json
{
  "isValid": false,
  "errors": [
    "상품 '상품1'의 재고가 부족합니다. 요청 수량: 10, 현재 재고: 5",
    "상품 ID 999에 해당하는 상품을 찾을 수 없습니다"
  ]
}
```

#### HTTP Status Codes
- `200 OK`: 성공 (검증 결과 반환)

---

## 데이터 모델

### Product (상품)
| 필드 | 타입 | 설명 |
|------|------|------|
| `id` | Long | 상품 ID (자동 생성) |
| `name` | String | 상품명 (필수, 최대 100자) |
| `description` | String | 상품 설명 (최대 500자) |
| `price` | BigDecimal | 가격 (필수, 정밀도 10.2) |
| `stock` | Integer | 재고 수량 (필수) |
| `category` | String | 카테고리 (최대 50자) |
| `brand` | String | 브랜드 (최대 100자) |
| `sku` | String | SKU 코드 (최대 20자) |
| `isActive` | Boolean | 활성 상태 (기본값: true) |
| `createdAt` | LocalDateTime | 생성일시 |
| `updatedAt` | LocalDateTime | 수정일시 |

### DiscountPolicy (할인 정책)
| 필드 | 타입 | 설명 |
|------|------|------|
| `id` | Long | 할인 정책 ID (자동 생성) |
| `name` | String | 정책명 (필수, 최대 100자) |
| `description` | String | 정책 설명 (최대 500자) |
| `discountType` | DiscountType | 할인 타입 (PERCENTAGE/FIXED_AMOUNT) |
| `discountTarget` | DiscountTarget | 할인 대상 |
| `discountValue` | BigDecimal | 할인 값 (필수) |
| `maxDiscountAmount` | BigDecimal | 최대 할인 금액 |
| `minOrderAmount` | BigDecimal | 최소 주문 금액 |
| `minQuantity` | Integer | 최소 수량 |
| `maxQuantity` | Integer | 최대 수량 |
| `targetProductId` | Long | 대상 상품 ID |
| `targetCategory` | String | 대상 카테고리 |
| `targetBrand` | String | 대상 브랜드 |
| `isActive` | Boolean | 활성 상태 (기본값: true) |
| `startDate` | LocalDateTime | 시작일시 |
| `endDate` | LocalDateTime | 종료일시 |
| `createdAt` | LocalDateTime | 생성일시 |
| `updatedAt` | LocalDateTime | 수정일시 |

### DiscountType (할인 타입)
- `PERCENTAGE`: 비율 할인 (10%, 20% 등)
- `FIXED_AMOUNT`: 고정 금액 할인 (1,000원, 5,000원 등)

### DiscountTarget (할인 대상)
- `PRODUCT`: 개별 상품 할인
- `CATEGORY`: 카테고리 할인
- `BRAND`: 브랜드 할인
- `PRODUCT_GROUP`: 상품 그룹 할인
- `QUANTITY`: 수량 할인
- `ORDER_AMOUNT`: 주문 금액 할인
- `PRODUCT_AMOUNT`: 상품 금액 할인

### CartItemRequest (장바구니 아이템 요청)
| 필드 | 타입 | 설명 |
|------|------|------|
| `productId` | Long | 상품 ID |
| `quantity` | Integer | 수량 |

### CartWithDiscountItemResponse (장바구니 아이템 응답)
| 필드 | 타입 | 설명 |
|------|------|------|
| `productId` | Long | 상품 ID |
| `productName` | String | 상품명 |
| `price` | BigDecimal | 단가 |
| `quantity` | Integer | 수량 |
| `totalPrice` | BigDecimal | 총 가격 |
| `discountAmount` | BigDecimal | 할인 금액 |
| `finalPrice` | BigDecimal | 최종 가격 |

---

## 에러 처리

### 일반적인 HTTP 상태 코드
- `200 OK`: 요청 성공
- `201 Created`: 리소스 생성 성공
- `204 No Content`: 요청 성공, 응답 본문 없음
- `400 Bad Request`: 잘못된 요청
- `404 Not Found`: 리소스를 찾을 수 없음
- `500 Internal Server Error`: 서버 내부 오류

### 에러 응답 형식
```json
{
  "timestamp": "2024-01-01T00:00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "에러 메시지",
  "path": "/api/endpoint"
}
```

---

## 사용 예시

### 1. 상품 생성 후 장바구니에 추가
```bash
# 1. 상품 생성
curl -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -d '{
    "name": "테스트 상품",
    "price": 10000,
    "stock": 100,
    "category": "전자제품"
  }'

# 2. 장바구니 계산
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

### 2. 장바구니 검증
```bash
curl -X POST http://localhost:8080/api/cart/validate \
  -H "Content-Type: application/json" \
  -d '{
    "items": [
      {
        "productId": 1,
        "quantity": 150
      }
    ]
  }'
```

---

## 주의사항

1. **BigDecimal 정밀도**: 가격과 할인 금액은 BigDecimal 타입을 사용하여 정밀한 계산을 보장합니다.
2. **CORS 설정**: 모든 도메인에서의 접근이 허용되어 있습니다.
3. **검증**: `/calculate` 엔드포인트는 상품 존재 여부와 재고를 검증하지만, `/calculate-without-validation`은 검증하지 않습니다.
4. **할인 정책**: 활성화된 할인 정책이 자동으로 적용됩니다.
5. **페이지네이션**: 상품 목록 조회 시 페이지네이션을 지원합니다.

---

## 개발 환경 설정

### 요구사항
- Java 17+
- Spring Boot 3.x
- Gradle
- H2 Database (개발용)

### 실행 방법
```bash
# 프로젝트 빌드
./gradlew build

# 애플리케이션 실행
./gradlew bootRun

# 테스트 실행
./gradlew test
```

### 데이터베이스
- H2 인메모리 데이터베이스 사용
- 애플리케이션 시작 시 샘플 데이터 자동 생성
- 접속 URL: `http://localhost:8080/h2-console`
