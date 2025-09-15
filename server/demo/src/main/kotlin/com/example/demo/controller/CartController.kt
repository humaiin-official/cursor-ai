package com.example.demo.controller

import com.example.demo.dto.CartRequest
import com.example.demo.dto.CartWithDiscountResponse
import com.example.demo.service.CartWithDiscountService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/cart")
@CrossOrigin(origins = ["*"])
class CartController(
    private val cartWithDiscountService: CartWithDiscountService
) {
    
    @PostMapping("/calculate")
    fun calculateCartTotal(@RequestBody cartRequest: CartRequest): ResponseEntity<CartWithDiscountResponse> {
        return try {
            val response = cartWithDiscountService.calculateCartTotalWithValidation(cartRequest)
            ResponseEntity.ok(response)
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().build()
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
        }
    }
    
    @PostMapping("/calculate-without-validation")
    fun calculateCartTotalWithoutValidation(@RequestBody cartRequest: CartRequest): ResponseEntity<CartWithDiscountResponse> {
        return try {
            val response = cartWithDiscountService.calculateCartTotal(cartRequest)
            ResponseEntity.ok(response)
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
        }
    }
    
    @PostMapping("/validate")
    fun validateCartItems(@RequestBody cartRequest: CartRequest): ResponseEntity<Map<String, Any>> {
        val validationErrors = cartWithDiscountService.validateCartItems(cartRequest)
        val response = mapOf(
            "isValid" to validationErrors.isEmpty(),
            "errors" to validationErrors
        )
        return ResponseEntity.ok(response)
    }
}
