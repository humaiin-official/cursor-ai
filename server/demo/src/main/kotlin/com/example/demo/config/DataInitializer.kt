package com.example.demo.config

import com.example.demo.entity.Product
import com.example.demo.repository.ProductRepository
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component
import java.math.BigDecimal

@Component
class DataInitializer(
    private val productRepository: ProductRepository
) : CommandLineRunner {
    
    override fun run(vararg args: String?) {
        if (productRepository.count() == 0L) {
            initializeProducts()
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
}
