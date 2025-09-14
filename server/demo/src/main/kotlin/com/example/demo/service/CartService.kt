package com.example.demo.service

import com.example.demo.dto.CartItemRequest
import com.example.demo.dto.CartItemResponse
import com.example.demo.dto.CartRequest
import com.example.demo.dto.CartResponse
import com.example.demo.entity.Product
import com.example.demo.repository.ProductRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Service
@Transactional(readOnly = true)
class CartService(
    private val productRepository: ProductRepository
) {
    
    fun calculateCartTotal(cartRequest: CartRequest): CartResponse {
        val cartItems = mutableListOf<CartItemResponse>()
        var totalAmount = BigDecimal.ZERO
        var totalItemCount = 0
        
        for (item in cartRequest.items) {
            val product = productRepository.findById(item.productId)
                .filter { it.isActive }
                .orElse(null)
            
            if (product != null) {
                val itemTotalPrice = product.price.multiply(BigDecimal(item.quantity))
                
                val cartItem = CartItemResponse(
                    productId = product.id!!,
                    productName = product.name,
                    price = product.price,
                    quantity = item.quantity,
                    totalPrice = itemTotalPrice
                )
                
                cartItems.add(cartItem)
                totalAmount = totalAmount.add(itemTotalPrice)
                totalItemCount += item.quantity
            }
        }
        
        return CartResponse(
            items = cartItems,
            totalAmount = totalAmount,
            totalItemCount = totalItemCount
        )
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
    
    fun calculateCartTotalWithValidation(cartRequest: CartRequest): CartResponse {
        val validationErrors = validateCartItems(cartRequest)
        
        if (validationErrors.isNotEmpty()) {
            throw IllegalArgumentException("장바구니 검증 실패: ${validationErrors.joinToString(", ")}")
        }
        
        return calculateCartTotal(cartRequest)
    }
}
