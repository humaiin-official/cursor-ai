package com.example.demo.dto

import java.math.BigDecimal

data class CartItemRequest(
    val productId: Long,
    val quantity: Int
)

data class CartRequest(
    val items: List<CartItemRequest>
)

data class CartItemResponse(
    val productId: Long,
    val productName: String,
    val price: BigDecimal,
    val quantity: Int,
    val totalPrice: BigDecimal
)

data class CartResponse(
    val items: List<CartItemResponse>,
    val totalAmount: BigDecimal,
    val totalItemCount: Int
)

data class CartWithDiscountItemResponse(
        val productId: Long,
        val productName: String,
        val price: BigDecimal,
        val quantity: Int,
        val totalPrice: BigDecimal,
        val discountAmount: BigDecimal = BigDecimal.ZERO,
        val finalPrice: BigDecimal = totalPrice
)

data class CartWithDiscountResponse(
    val items: List<CartWithDiscountItemResponse>,
    val totalAmount: BigDecimal,
    val totalItemCount: Int,
    val totalDiscountAmount: BigDecimal = BigDecimal.ZERO,
    val finalTotalAmount: BigDecimal = totalAmount
)
