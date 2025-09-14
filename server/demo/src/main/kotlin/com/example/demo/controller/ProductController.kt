package com.example.demo.controller

import com.example.demo.dto.ProductDto
import com.example.demo.dto.ProductListResponse
import com.example.demo.dto.ProductSearchRequest
import com.example.demo.service.ProductService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = ["*"])
class ProductController(
    private val productService: ProductService
) {
    
    @GetMapping
    fun getAllProducts(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int,
        @RequestParam(defaultValue = "createdAt") sortBy: String,
        @RequestParam(defaultValue = "desc") direction: String
    ): ResponseEntity<ProductListResponse> {
        val response = productService.getAllProducts(page, size, sortBy, direction)
        return ResponseEntity.ok(response)
    }
    
    @GetMapping("/search")
    fun searchProducts(
        @RequestParam(required = false) keyword: String?,
        @RequestParam(required = false) category: String?,
        @RequestParam(required = false) brand: String?,
        @RequestParam(required = false) minPrice: String?,
        @RequestParam(required = false) maxPrice: String?,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int
    ): ResponseEntity<ProductListResponse> {
        val searchRequest = ProductSearchRequest(
            keyword = keyword,
            category = category,
            brand = brand,
            minPrice = minPrice?.let { java.math.BigDecimal(it) },
            maxPrice = maxPrice?.let { java.math.BigDecimal(it) }
        )
        
        val response = productService.searchProducts(searchRequest, page, size)
        return ResponseEntity.ok(response)
    }
    
    @GetMapping("/{id}")
    fun getProductById(@PathVariable id: Long): ResponseEntity<ProductDto> {
        val product = productService.getProductById(id)
        return if (product != null) {
            ResponseEntity.ok(product)
        } else {
            ResponseEntity.notFound().build()
        }
    }
    
    @GetMapping("/category/{category}")
    fun getProductsByCategory(@PathVariable category: String): ResponseEntity<List<ProductDto>> {
        val products = productService.getProductsByCategory(category)
        return ResponseEntity.ok(products)
    }
    
    @GetMapping("/brand/{brand}")
    fun getProductsByBrand(@PathVariable brand: String): ResponseEntity<List<ProductDto>> {
        val products = productService.getProductsByBrand(brand)
        return ResponseEntity.ok(products)
    }
    
    @PostMapping
    fun createProduct(@RequestBody productDto: ProductDto): ResponseEntity<ProductDto> {
        val createdProduct = productService.createProduct(productDto)
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct)
    }
    
    @PutMapping("/{id}")
    fun updateProduct(
        @PathVariable id: Long,
        @RequestBody productDto: ProductDto
    ): ResponseEntity<ProductDto> {
        val updatedProduct = productService.updateProduct(id, productDto)
        return if (updatedProduct != null) {
            ResponseEntity.ok(updatedProduct)
        } else {
            ResponseEntity.notFound().build()
        }
    }
    
    @DeleteMapping("/{id}")
    fun deleteProduct(@PathVariable id: Long): ResponseEntity<Void> {
        val deleted = productService.deleteProduct(id)
        return if (deleted) {
            ResponseEntity.noContent().build()
        } else {
            ResponseEntity.notFound().build()
        }
    }
}
