package com.example.demo.dto

import com.example.demo.entity.Product
import java.math.BigDecimal
import java.time.LocalDateTime

data class ProductDto(
    val id: Long? = null,
    val name: String,
    val description: String? = null,
    val price: BigDecimal,
    val stock: Int,
    val category: String? = null,
    val brand: String? = null,
    val sku: String? = null,
    val isActive: Boolean = true,
    val createdAt: LocalDateTime? = null,
    val updatedAt: LocalDateTime? = null
) {
    companion object {
        fun fromEntity(product: Product): ProductDto {
            return ProductDto(
                id = product.id,
                name = product.name,
                description = product.description,
                price = product.price,
                stock = product.stock,
                category = product.category,
                brand = product.brand,
                sku = product.sku,
                isActive = product.isActive,
                createdAt = product.createdAt,
                updatedAt = product.updatedAt
            )
        }
    }
}

data class ProductListResponse(
    val products: List<ProductDto>,
    val totalCount: Long
)

data class ProductSearchRequest(
    val keyword: String? = null,
    val category: String? = null,
    val brand: String? = null,
    val minPrice: BigDecimal? = null,
    val maxPrice: BigDecimal? = null
)
