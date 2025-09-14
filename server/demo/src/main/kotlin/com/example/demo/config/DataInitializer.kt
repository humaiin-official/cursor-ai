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
        if (productRepository.count() == 0L) {
            initializeProducts()
        }
        if (discountPolicyRepository.count() == 0L) {
            initializeDiscountPolicies()
        }
    }
    
    private fun initializeProducts() {
        val products = listOf(
            Product(
                name = "iPhone 15 Pro",
                description = "Apple의 최신 스마트폰, A17 Pro 칩셋 탑재",
                price = BigDecimal("1290000"),
                stock = 50,
                category = "스마트폰",
                brand = "Apple",
                sku = "IPH15PRO-128-BLK"
            ),
            Product(
                name = "Galaxy S24 Ultra",
                description = "삼성의 플래그십 스마트폰, S Pen 내장",
                price = BigDecimal("1190000"),
                stock = 30,
                category = "스마트폰",
                brand = "Samsung",
                sku = "GAL-S24U-256-BLK"
            ),
            Product(
                name = "MacBook Air M3",
                description = "Apple M3 칩셋을 탑재한 초경량 노트북",
                price = BigDecimal("1590000"),
                stock = 25,
                category = "노트북",
                brand = "Apple",
                sku = "MBA-M3-256-SLV"
            ),
            Product(
                name = "Galaxy Book4 Pro",
                description = "삼성의 고성능 노트북, AMOLED 디스플레이",
                price = BigDecimal("1390000"),
                stock = 20,
                category = "노트북",
                brand = "Samsung",
                sku = "GB4P-512-BLK"
            ),
            Product(
                name = "AirPods Pro 2세대",
                description = "Apple의 노이즈 캔슬링 이어폰",
                price = BigDecimal("329000"),
                stock = 100,
                category = "이어폰",
                brand = "Apple",
                sku = "APP2-WHT"
            ),
            Product(
                name = "Galaxy Buds2 Pro",
                description = "삼성의 프리미엄 무선 이어폰",
                price = BigDecimal("199000"),
                stock = 80,
                category = "이어폰",
                brand = "Samsung",
                sku = "GB2P-BLK"
            ),
            Product(
                name = "iPad Air 5세대",
                description = "Apple M1 칩셋을 탑재한 태블릿",
                price = BigDecimal("799000"),
                stock = 40,
                category = "태블릿",
                brand = "Apple",
                sku = "IPA5-64-WHT"
            ),
            Product(
                name = "Galaxy Tab S9",
                description = "삼성의 고성능 태블릿, S Pen 포함",
                price = BigDecimal("899000"),
                stock = 35,
                category = "태블릿",
                brand = "Samsung",
                sku = "GTS9-128-BLK"
            ),
            Product(
                name = "Apple Watch Series 9",
                description = "Apple의 최신 스마트워치",
                price = BigDecimal("599000"),
                stock = 60,
                category = "스마트워치",
                brand = "Apple",
                sku = "AWS9-45-ALM"
            ),
            Product(
                name = "Galaxy Watch6 Classic",
                description = "삼성의 프리미엄 스마트워치",
                price = BigDecimal("399000"),
                stock = 45,
                category = "스마트워치",
                brand = "Samsung",
                sku = "GW6C-47-BLK"
            )
        )
        
        productRepository.saveAll(products)
        println("테스트 상품 데이터가 초기화되었습니다. 총 ${products.size}개의 상품이 추가되었습니다.")
    }
    
    private fun initializeDiscountPolicies() {
        val discountPolicies = listOf(
            // 개별 상품 할인 - iPhone 15 Pro 10% 할인
            DiscountPolicy(
                name = "iPhone 15 Pro 특가 할인",
                description = "iPhone 15 Pro 10% 할인",
                discountType = DiscountType.PERCENTAGE,
                discountTarget = DiscountTarget.PRODUCT,
                discountValue = BigDecimal("10"),
                maxDiscountAmount = BigDecimal("100000"),
                targetProductId = 1L,
                isActive = true
            ),
            
            // 카테고리 할인 - 스마트폰 카테고리 5% 할인
            DiscountPolicy(
                name = "스마트폰 카테고리 할인",
                description = "스마트폰 카테고리 5% 할인",
                discountType = DiscountType.PERCENTAGE,
                discountTarget = DiscountTarget.CATEGORY,
                discountValue = BigDecimal("5"),
                targetCategory = "스마트폰",
                isActive = true
            ),
            
            // 브랜드 할인 - Apple 브랜드 3% 할인
            DiscountPolicy(
                name = "Apple 브랜드 할인",
                description = "Apple 브랜드 3% 할인",
                discountType = DiscountType.PERCENTAGE,
                discountTarget = DiscountTarget.BRAND,
                discountValue = BigDecimal("3"),
                targetBrand = "Apple",
                isActive = true
            ),
            
            // 수량 할인 - 2개 이상 구매 시 15% 할인
            DiscountPolicy(
                name = "수량 할인",
                description = "2개 이상 구매 시 15% 할인",
                discountType = DiscountType.PERCENTAGE,
                discountTarget = DiscountTarget.QUANTITY,
                discountValue = BigDecimal("15"),
                minQuantity = 2,
                isActive = true
            ),
            
            // 주문 금액 할인 - 100만원 이상 구매 시 5,000원 할인
            DiscountPolicy(
                name = "고액 주문 할인",
                description = "100만원 이상 구매 시 5,000원 할인",
                discountType = DiscountType.FIXED_AMOUNT,
                discountTarget = DiscountTarget.ORDER_AMOUNT,
                discountValue = BigDecimal("5000"),
                minOrderAmount = BigDecimal("1000000"),
                isActive = true
            ),
            
            // 상품 금액 할인 - 50만원 이상 상품 10% 할인
            DiscountPolicy(
                name = "고가 상품 할인",
                description = "50만원 이상 상품 10% 할인",
                discountType = DiscountType.PERCENTAGE,
                discountTarget = DiscountTarget.PRODUCT_AMOUNT,
                discountValue = BigDecimal("10"),
                minOrderAmount = BigDecimal("500000"),
                isActive = true
            ),
            
            // 이어폰 카테고리 20% 할인 (최대 50,000원)
            DiscountPolicy(
                name = "이어폰 특가 할인",
                description = "이어폰 카테고리 20% 할인 (최대 50,000원)",
                discountType = DiscountType.PERCENTAGE,
                discountTarget = DiscountTarget.CATEGORY,
                discountValue = BigDecimal("20"),
                maxDiscountAmount = BigDecimal("50000"),
                targetCategory = "이어폰",
                isActive = true
            ),
            
            // Samsung 브랜드 2개 이상 구매 시 10% 할인
            DiscountPolicy(
                name = "Samsung 수량 할인",
                description = "Samsung 브랜드 2개 이상 구매 시 10% 할인",
                discountType = DiscountType.PERCENTAGE,
                discountTarget = DiscountTarget.BRAND,
                discountValue = BigDecimal("10"),
                minQuantity = 2,
                targetBrand = "Samsung",
                isActive = true
            )
        )
        
        discountPolicyRepository.saveAll(discountPolicies)
        println("테스트 할인 정책 데이터가 초기화되었습니다. 총 ${discountPolicies.size}개의 할인 정책이 추가되었습니다.")
    }
}
