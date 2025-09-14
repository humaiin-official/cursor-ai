package com.example.demo.service

import com.example.demo.dto.ProductDto
import com.example.demo.dto.ProductListResponse
import com.example.demo.dto.ProductSearchRequest
import com.example.demo.entity.Product
import com.example.demo.repository.ProductRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Service
@Transactional(readOnly = true)
class ProductService(
    private val productRepository: ProductRepository
) {
    
    fun getAllProducts(page: Int = 0, size: Int = 20, sortBy: String = "createdAt", direction: String = "desc"): ProductListResponse {
        val sort = Sort.by(Sort.Direction.fromString(direction), sortBy)
        val pageable: Pageable = PageRequest.of(page, size, sort)
        
        val productPage = productRepository.findByIsActiveTrue(pageable)
        val productDtos = productPage.content.map { ProductDto.fromEntity(it) }
        
        return ProductListResponse(
            products = productDtos,
            totalCount = productPage.totalElements
        )
    }
    
    fun getProductById(id: Long): ProductDto? {
        return productRepository.findById(id)
            .filter { it.isActive }
            .map { ProductDto.fromEntity(it) }
            .orElse(null)
    }
    
    @Transactional
    fun createProduct(productDto: ProductDto): ProductDto {
        val product = Product(
            name = productDto.name,
            description = productDto.description,
            price = productDto.price,
            stock = productDto.stock,
            category = productDto.category,
            brand = productDto.brand,
            sku = productDto.sku,
            isActive = productDto.isActive
        )
        
        val savedProduct = productRepository.save(product)
        return ProductDto.fromEntity(savedProduct)
    }
    
    @Transactional
    fun updateProduct(id: Long, productDto: ProductDto): ProductDto? {
        return productRepository.findById(id)
            .filter { it.isActive }
            .map { existingProduct ->
                val updatedProduct = existingProduct.copy(
                    name = productDto.name,
                    description = productDto.description,
                    price = productDto.price,
                    stock = productDto.stock,
                    category = productDto.category,
                    brand = productDto.brand,
                    sku = productDto.sku,
                    isActive = productDto.isActive,
                    updatedAt = java.time.LocalDateTime.now()
                )
                ProductDto.fromEntity(productRepository.save(updatedProduct))
            }
            .orElse(null)
    }
    
    @Transactional
    fun deleteProduct(id: Long): Boolean {
        return productRepository.findById(id)
            .filter { it.isActive }
            .map { product ->
                val updatedProduct = product.copy(isActive = false, updatedAt = java.time.LocalDateTime.now())
                productRepository.save(updatedProduct)
                true
            }
            .orElse(false)
    }
}
