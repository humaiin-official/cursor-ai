package com.example.demo.service

import com.example.demo.dto.CartItemRequest
import com.example.demo.dto.CartItemResponse
import com.example.demo.dto.CartRequest
import com.example.demo.dto.CartResponse
import com.example.demo.dto.CartWithDiscountItemResponse
import com.example.demo.dto.CartWithDiscountResponse
import com.example.demo.entity.DiscountPolicy
import com.example.demo.entity.DiscountTarget
import com.example.demo.entity.DiscountType
import com.example.demo.entity.Product
import com.example.demo.repository.ProductRepository
import com.example.demo.repository.DiscountPolicyRepository
import java.math.BigDecimal
import java.time.LocalDateTime
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class CartWithDiscountService(
    private val productRepository: ProductRepository,
    private val discountPolicyRepository: DiscountPolicyRepository
) {

    fun calculateCartTotal(cartRequest: CartRequest): CartWithDiscountResponse {
        val cartItems = mutableListOf<CartWithDiscountItemResponse>()
        var totalAmount = BigDecimal.ZERO
        var totalItemCount = 0
        var totalDiscountAmount = BigDecimal.ZERO
        
        // 활성 할인 정책 조회
        val activeDiscountPolicies = discountPolicyRepository.findActivePoliciesByDate(LocalDateTime.now())
        
        for (item in cartRequest.items) {
            val product = productRepository.findById(item.productId)
                .filter { it.isActive }
                .orElse(null)
            
            if (product != null) {
                val itemTotalPrice = product.price.multiply(BigDecimal(item.quantity))
                
                // 해당 상품에 적용 가능한 할인 정책 찾기
                val applicablePolicies = findApplicableDiscountPolicies(
                    product, item, itemTotalPrice, activeDiscountPolicies
                )
                
                // 할인 금액 계산
                val itemDiscountAmount = calculateItemDiscountAmount(
                    product, item, itemTotalPrice, applicablePolicies
                )
                
                val finalPrice = itemTotalPrice.subtract(itemDiscountAmount)
                
                val cartItem = CartWithDiscountItemResponse(
                    productId = product.id!!,
                    productName = product.name,
                    price = product.price,
                    quantity = item.quantity,
                    totalPrice = itemTotalPrice,
                    discountAmount = itemDiscountAmount,
                    finalPrice = finalPrice
                )
                
                cartItems.add(cartItem)
                totalAmount = totalAmount.add(itemTotalPrice)
                totalDiscountAmount = totalDiscountAmount.add(itemDiscountAmount)
                totalItemCount += item.quantity
            }
        }
        
        // 주문 금액별 할인 적용
        val orderDiscountAmount = calculateOrderDiscountAmount(totalAmount, activeDiscountPolicies)
        totalDiscountAmount = totalDiscountAmount.add(orderDiscountAmount)
        
        val finalTotalAmount = totalAmount.subtract(totalDiscountAmount)
        
        return CartWithDiscountResponse(
            items = cartItems,
            totalAmount = totalAmount,
            totalItemCount = totalItemCount,
            totalDiscountAmount = totalDiscountAmount,
            finalTotalAmount = finalTotalAmount
        )
    }
    
    private fun findApplicableDiscountPolicies(
        product: Product,
        item: CartItemRequest,
        itemTotalPrice: BigDecimal,
        activePolicies: List<DiscountPolicy>
    ): List<DiscountPolicy> {
        return activePolicies.filter { policy ->
            when (policy.discountTarget) {
                DiscountTarget.PRODUCT -> {
                    policy.targetProductId == product.id
                }
                DiscountTarget.QUANTITY -> {
                    policy.minQuantity?.let { item.quantity >= it } ?: true &&
                    policy.maxQuantity?.let { item.quantity <= it } ?: true
                }
                DiscountTarget.PRODUCT_AMOUNT -> {
                    policy.minOrderAmount?.let { itemTotalPrice >= it } ?: true
                }
                else -> false
            }
        }
    }
    
    private fun calculateItemDiscountAmount(
        product: Product,
        item: CartItemRequest,
        itemTotalPrice: BigDecimal,
        applicablePolicies: List<DiscountPolicy>
    ): BigDecimal {
        var totalDiscountAmount = BigDecimal.ZERO
        
        for (policy in applicablePolicies) {
            val discountAmount = when (policy.discountType) {
                DiscountType.PERCENTAGE -> {
                    val percentageDiscount = itemTotalPrice.multiply(policy.discountValue.divide(BigDecimal("100"), 2, java.math.RoundingMode.HALF_UP))
                    policy.maxDiscountAmount?.let { 
                        percentageDiscount.min(it) 
                    } ?: percentageDiscount
                }
                DiscountType.FIXED_AMOUNT -> {
                    policy.discountValue
                }
            }
            totalDiscountAmount = totalDiscountAmount.add(discountAmount)
        }
        
        return totalDiscountAmount
    }
    
    private fun calculateOrderDiscountAmount(
        totalAmount: BigDecimal,
        activePolicies: List<DiscountPolicy>
    ): BigDecimal {
        val orderPolicies = activePolicies.filter { it.discountTarget == DiscountTarget.ORDER_AMOUNT }
        
        for (policy in orderPolicies) {
            if (policy.minOrderAmount?.let { totalAmount >= it } ?: true) {
                return when (policy.discountType) {
                    DiscountType.PERCENTAGE -> {
                        val percentageDiscount = totalAmount.multiply(policy.discountValue.divide(BigDecimal("100"), 2, java.math.RoundingMode.HALF_UP))
                        policy.maxDiscountAmount?.let { 
                            percentageDiscount.min(it) 
                        } ?: percentageDiscount
                    }
                    DiscountType.FIXED_AMOUNT -> {
                        policy.discountValue
                    }
                }
            }
        }
        
        return BigDecimal.ZERO
    }

    fun validateCartItems(cartRequest: CartRequest): List<String> {
        val errors = mutableListOf<String>()
        
        for (item in cartRequest.items) {
            val product = productRepository.findById(item.productId)
                .filter { it.isActive }
                .orElse(null)
            
            when {
                product == null -> {
                    errors.add("상품 ID ${item.productId}에 해당하는 상품을 찾을 수 없습니다.")
                }
                !product.isActive -> {
                    errors.add("상품 '${product.name}'은 현재 판매되지 않습니다.")
                }
                item.quantity <= 0 -> {
                    errors.add("상품 '${product.name}'의 수량은 1개 이상이어야 합니다.")
                }
                item.quantity > product.stock -> {
                    errors.add("상품 '${product.name}'의 재고가 부족합니다. (요청: ${item.quantity}개, 재고: ${product.stock}개)")
                }
            }
        }
        
        return errors
    }

    fun calculateCartTotalWithValidation(cartRequest: CartRequest): CartWithDiscountResponse {
        val validationErrors = validateCartItems(cartRequest)
        
        if (validationErrors.isNotEmpty()) {
            throw IllegalArgumentException("장바구니 검증 실패: ${validationErrors.joinToString(", ")}")
        }
        
        return calculateCartTotal(cartRequest)
    }
}
