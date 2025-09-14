package com.example.demo.controller

import com.example.demo.dto.CartRequest
import com.example.demo.dto.CartResponse
import com.example.demo.service.CartService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/cart")
@CrossOrigin(origins = ["*"])
class CartController(
    private val cartService: CartService
) {
    
    @PostMapping("/calculate")
    fun calculateCartTotal(@RequestBody cartRequest: CartRequest): ResponseEntity<CartResponse> {
        return try {
            val response = cartService.calculateCartTotalWithValidation(cartRequest)
            ResponseEntity.ok(response)
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().build()
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
        }
    }
    
    @PostMapping("/calculate-without-validation")
    fun calculateCartTotalWithoutValidation(@RequestBody cartRequest: CartRequest): ResponseEntity<CartResponse> {
        return try {
            val response = cartService.calculateCartTotal(cartRequest)
            ResponseEntity.ok(response)
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
        }
    }
    
    @PostMapping("/validate")
    fun validateCartItems(@RequestBody cartRequest: CartRequest): ResponseEntity<Map<String, Any>> {
        val validationErrors = cartService.validateCartItems(cartRequest)
        val response = mapOf(
            "isValid" to validationErrors.isEmpty(),
            "errors" to validationErrors
        )
        return ResponseEntity.ok(response)
    }
}
