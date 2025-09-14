package com.example.demo.repository

import com.example.demo.entity.Product
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface ProductRepository : JpaRepository<Product, Long> {
    
    fun findByIsActiveTrue(): List<Product>
    
    fun findByCategoryAndIsActiveTrue(category: String): List<Product>
    
    fun findByNameContainingIgnoreCaseAndIsActiveTrue(name: String): List<Product>
    
    @Query("SELECT p FROM Product p WHERE p.isActive = true AND (p.name LIKE %:keyword% OR p.description LIKE %:keyword%)")
    fun searchByKeyword(@Param("keyword") keyword: String): List<Product>
    
    fun findByBrandAndIsActiveTrue(brand: String): List<Product>
}
