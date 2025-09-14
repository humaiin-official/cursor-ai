package com.example.demo.repository

import com.example.demo.entity.DiscountPolicy
import com.example.demo.entity.DiscountTarget
import com.example.demo.entity.DiscountType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface DiscountPolicyRepository : JpaRepository<DiscountPolicy, Long> {
    
    @Query("""
        SELECT d FROM DiscountPolicy d 
        WHERE d.isActive = true 
        AND (d.startDate IS NULL OR d.startDate <= :currentTime)
        AND (d.endDate IS NULL OR d.endDate >= :currentTime)
    """)
    fun findActivePoliciesByDate(@Param("currentTime") currentTime: LocalDateTime): List<DiscountPolicy>
}
