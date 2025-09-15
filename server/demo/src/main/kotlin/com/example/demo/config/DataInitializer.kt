package com.example.demo.config

import com.example.demo.entity.DiscountPolicy
import com.example.demo.entity.DiscountTarget
import com.example.demo.entity.DiscountType
import com.example.demo.entity.Product
import com.example.demo.repository.DiscountPolicyRepository
import com.example.demo.repository.ProductRepository
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.time.LocalDateTime

@Component
class DataInitializer(
    private val productRepository: ProductRepository,
    private val discountPolicyRepository: DiscountPolicyRepository
) : CommandLineRunner {

    override fun run(vararg args: String?) {
        initializeProducts()
        initializeDiscountPolicies()
    }

    private fun initializeProducts() {
        // 기존 데이터가 있는지 확인
        if (productRepository.count() > 0) {
            return
        }

        val products = listOf(
            // 삼성 브랜드 상품들
            Product(
                id = null,
                name = "삼성 갤럭시 S24",
                description = "삼성의 최신 플래그십 스마트폰",
                price = BigDecimal("1200000"),
                stock = 50,
                category = "스마트폰",
                brand = "삼성",
                sku = "SAMSUNG-S24",
                isActive = true,
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now()
            ),
            Product(
                id = null,
                name = "삼성 갤럭시 버즈2",
                description = "삼성 무선 이어폰",
                price = BigDecimal("150000"),
                stock = 100,
                category = "이어폰",
                brand = "삼성",
                sku = "SAMSUNG-BUDS2",
                isActive = true,
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now()
            ),
            Product(
                id = null,
                name = "삼성 갤럭시 워치6",
                description = "삼성 스마트워치",
                price = BigDecimal("350000"),
                stock = 30,
                category = "스마트워치",
                brand = "삼성",
                sku = "SAMSUNG-WATCH6",
                isActive = true,
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now()
            ),

            // 애플 브랜드 상품들
            Product(
                id = null,
                name = "아이폰 15 Pro",
                description = "애플의 최신 플래그십 스마트폰",
                price = BigDecimal("1500000"),
                stock = 40,
                category = "스마트폰",
                brand = "애플",
                sku = "APPLE-IPHONE15",
                isActive = true,
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now()
            ),
            Product(
                id = null,
                name = "에어팟 프로 2",
                description = "애플 무선 이어폰",
                price = BigDecimal("300000"),
                stock = 80,
                category = "이어폰",
                brand = "애플",
                sku = "APPLE-AIRPODS2",
                isActive = true,
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now()
            ),
            Product(
                id = null,
                name = "애플워치 시리즈 9",
                description = "애플 스마트워치",
                price = BigDecimal("500000"),
                stock = 25,
                category = "스마트워치",
                brand = "애플",
                sku = "APPLE-WATCH9",
                isActive = true,
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now()
            ),

            // LG 브랜드 상품들
            Product(
                id = null,
                name = "LG 그램 노트북",
                description = "LG 초경량 노트북",
                price = BigDecimal("1800000"),
                stock = 20,
                category = "노트북",
                brand = "LG",
                sku = "LG-GRAM",
                isActive = true,
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now()
            ),
            Product(
                id = null,
                name = "LG 올레드 TV 65인치",
                description = "LG 프리미엄 TV",
                price = BigDecimal("2500000"),
                stock = 15,
                category = "TV",
                brand = "LG",
                sku = "LG-OLED-TV65",
                isActive = true,
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now()
            ),

            // 기타 카테고리 상품들 (할인 테스트용)
            Product(
                id = null,
                name = "무선 마우스",
                description = "일반 무선 마우스",
                price = BigDecimal("50000"),
                stock = 200,
                category = "컴퓨터 액세서리",
                brand = "로지텍",
                sku = "LOGITECH-MOUSE",
                isActive = true,
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now()
            ),
            Product(
                id = null,
                name = "무선 키보드",
                description = "일반 무선 키보드",
                price = BigDecimal("80000"),
                stock = 150,
                category = "컴퓨터 액세서리",
                brand = "로지텍",
                sku = "LOGITECH-KEY",
                isActive = true,
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now()
            ),
            Product(
                id = null,
                name = "USB 케이블",
                description = "USB-C 케이블",
                price = BigDecimal("15000"),
                stock = 300,
                category = "컴퓨터 액세서리",
                brand = "벨킨",
                sku = "BELKIN-USBC",
                isActive = true,
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now()
            )
        )

        productRepository.saveAll(products)
        println("상품 데이터 초기화 완료: ${products.size}개 상품")
    }

    private fun initializeDiscountPolicies() {
        // 기존 데이터가 있는지 확인
        if (discountPolicyRepository.count() > 0) {
            return
        }

        val discountPolicies = listOf(
            // 삼성 브랜드 15% 할인 정책
            DiscountPolicy(
                id = null,
                name = "삼성 브랜드 15% 할인",
                description = "삼성 브랜드 상품 전체 15% 할인",
                discountType = DiscountType.PERCENTAGE,
                discountTarget = DiscountTarget.BRAND,
                discountValue = BigDecimal("15"),
                maxDiscountAmount = BigDecimal("200000"), // 최대 20만원 할인
                minOrderAmount = null,
                minQuantity = null,
                maxQuantity = null,
                targetProductId = null,
                targetCategory = null,
                targetBrand = "삼성",
                isActive = true,
                startDate = LocalDateTime.now().minusDays(1),
                endDate = LocalDateTime.now().plusMonths(1),
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now()
            ),

            // 애플 브랜드 15% 할인 정책
            DiscountPolicy(
                id = null,
                name = "애플 브랜드 15% 할인",
                description = "애플 브랜드 상품 전체 15% 할인",
                discountType = DiscountType.PERCENTAGE,
                discountTarget = DiscountTarget.BRAND,
                discountValue = BigDecimal("15"),
                maxDiscountAmount = BigDecimal("300000"), // 최대 30만원 할인
                minOrderAmount = null,
                minQuantity = null,
                maxQuantity = null,
                targetProductId = null,
                targetCategory = null,
                targetBrand = "애플",
                isActive = true,
                startDate = LocalDateTime.now().minusDays(1),
                endDate = LocalDateTime.now().plusMonths(1),
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now()
            ),

            // 컴퓨터 액세서리 카테고리 1000원 고정 할인 정책
            DiscountPolicy(
                id = null,
                name = "컴퓨터 액세서리 1000원 할인",
                description = "컴퓨터 액세서리 카테고리 상품 1000원 할인",
                discountType = DiscountType.FIXED_AMOUNT,
                discountTarget = DiscountTarget.CATEGORY,
                discountValue = BigDecimal("1000"),
                maxDiscountAmount = null,
                minOrderAmount = null,
                minQuantity = null,
                maxQuantity = null,
                targetProductId = null,
                targetCategory = "컴퓨터 액세서리",
                targetBrand = null,
                isActive = true,
                startDate = LocalDateTime.now().minusDays(1),
                endDate = LocalDateTime.now().plusMonths(1),
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now()
            ),

            // 스마트폰 카테고리 1000원 고정 할인 정책
            DiscountPolicy(
                id = null,
                name = "스마트폰 카테고리 1000원 할인",
                description = "스마트폰 카테고리 상품 1000원 할인",
                discountType = DiscountType.FIXED_AMOUNT,
                discountTarget = DiscountTarget.CATEGORY,
                discountValue = BigDecimal("1000"),
                maxDiscountAmount = null,
                minOrderAmount = null,
                minQuantity = null,
                maxQuantity = null,
                targetProductId = null,
                targetCategory = "스마트폰",
                targetBrand = null,
                isActive = true,
                startDate = LocalDateTime.now().minusDays(1),
                endDate = LocalDateTime.now().plusMonths(1),
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now()
            ),

            // 추가 테스트용 할인 정책들
            DiscountPolicy(
                id = null,
                name = "LG 브랜드 10% 할인",
                description = "LG 브랜드 상품 전체 10% 할인",
                discountType = DiscountType.PERCENTAGE,
                discountTarget = DiscountTarget.BRAND,
                discountValue = BigDecimal("10"),
                maxDiscountAmount = BigDecimal("100000"), // 최대 10만원 할인
                minOrderAmount = null,
                minQuantity = null,
                maxQuantity = null,
                targetProductId = null,
                targetCategory = null,
                targetBrand = "LG",
                isActive = true,
                startDate = LocalDateTime.now().minusDays(1),
                endDate = LocalDateTime.now().plusMonths(1),
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now()
            ),

            // 수량 할인 정책 (2개 이상 구매 시 5% 할인)
            DiscountPolicy(
                id = null,
                name = "수량 할인 - 2개 이상 5% 할인",
                description = "2개 이상 구매 시 5% 할인",
                discountType = DiscountType.PERCENTAGE,
                discountTarget = DiscountTarget.QUANTITY,
                discountValue = BigDecimal("5"),
                maxDiscountAmount = null,
                minOrderAmount = null,
                minQuantity = 2,
                maxQuantity = null,
                targetProductId = null,
                targetCategory = null,
                targetBrand = null,
                isActive = true,
                startDate = LocalDateTime.now().minusDays(1),
                endDate = LocalDateTime.now().plusMonths(1),
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now()
            ),

            // 주문 금액 할인 정책 (10만원 이상 구매 시 5% 할인)
            DiscountPolicy(
                id = null,
                name = "주문 금액 할인 - 10만원 이상 5% 할인",
                description = "10만원 이상 구매 시 5% 할인",
                discountType = DiscountType.PERCENTAGE,
                discountTarget = DiscountTarget.ORDER_AMOUNT,
                discountValue = BigDecimal("5"),
                maxDiscountAmount = BigDecimal("50000"), // 최대 5만원 할인
                minOrderAmount = BigDecimal("100000"),
                minQuantity = null,
                maxQuantity = null,
                targetProductId = null,
                targetCategory = null,
                targetBrand = null,
                isActive = true,
                startDate = LocalDateTime.now().minusDays(1),
                endDate = LocalDateTime.now().plusMonths(1),
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now()
            )
        )

        discountPolicyRepository.saveAll(discountPolicies)
        println("할인 정책 데이터 초기화 완료: ${discountPolicies.size}개 정책")
    }
}
