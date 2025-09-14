package com.example.demo.entity

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
@Table(name = "discount_policies")
data class DiscountPolicy(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    
    @Column(nullable = false, length = 100)
    val name: String,
    
    @Column(length = 500)
    val description: String? = null,
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val discountType: DiscountType,
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val discountTarget: DiscountTarget,
    
    @Column(nullable = false, precision = 10, scale = 2)
    val discountValue: BigDecimal,
    
    @Column(precision = 10, scale = 2)
    val maxDiscountAmount: BigDecimal? = null,
    
    @Column(precision = 10, scale = 2)
    val minOrderAmount: BigDecimal? = null,
    
    @Column
    val minQuantity: Int? = null,
    
    @Column
    val maxQuantity: Int? = null,
    
    @Column(length = 50)
    val targetProductId: Long? = null,
    
    @Column(length = 50)
    val targetCategory: String? = null,
    
    @Column(length = 50)
    val targetBrand: String? = null,
    
    @Column(name = "is_active")
    val isActive: Boolean = true,
    
    @Column(name = "start_date")
    val startDate: LocalDateTime? = null,
    
    @Column(name = "end_date")
    val endDate: LocalDateTime? = null,
    
    @Column(name = "created_at")
    val createdAt: LocalDateTime = LocalDateTime.now(),
    
    @Column(name = "updated_at")
    val updatedAt: LocalDateTime = LocalDateTime.now()
)

enum class DiscountType {
    PERCENTAGE,      // 비율 할인 (10%, 20% 등)
    FIXED_AMOUNT     // 고정 금액 할인 (1,000원, 5,000원 등)
}

enum class DiscountTarget {
    PRODUCT,         // 개별 상품 할인
    CATEGORY,        // 카테고리 할인
    BRAND,           // 브랜드 할인
    PRODUCT_GROUP,   // 상품 그룹 할인
    QUANTITY,        // 수량 할인
    ORDER_AMOUNT,    // 주문 금액 할인
    PRODUCT_AMOUNT   // 상품 금액 할인
}
