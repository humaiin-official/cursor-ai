package com.example.demo.service

import com.example.demo.dto.CartItemResponse
import com.example.demo.dto.CartRequest
import com.example.demo.dto.CartResponse
import com.example.demo.dto.CartWithDiscountResponse
import com.example.demo.repository.ProductRepository
import com.example.demo.repository.DiscountPolicyRepository
import java.math.BigDecimal
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional(readOnly = true)
class CartWithDiscountService(
    private val productRepository: ProductRepository,
    private val discountPolicyRepository: DiscountPolicyRepository
) {

    fun calculateCartTotal(cartRequest: CartRequest): CartWithDiscountResponse {
        TODO("Not yet implemented")
    }

    fun validateCartItems(cartRequest: CartRequest): List<String> {
        TODO("Not yet implemented")
    }

    fun calculateCartTotalWithValidation(cartRequest: CartRequest): CartWithDiscountResponse {
        TODO("Not yet implemented")
    }
}
