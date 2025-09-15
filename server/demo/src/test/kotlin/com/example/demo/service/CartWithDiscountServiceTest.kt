package com.example.demo.service

import com.example.demo.dto.CartItemRequest
import com.example.demo.dto.CartRequest
import com.example.demo.dto.CartWithDiscountItemResponse
import com.example.demo.dto.CartWithDiscountResponse
import com.example.demo.entity.DiscountPolicy
import com.example.demo.entity.DiscountTarget
import com.example.demo.entity.DiscountType
import com.example.demo.entity.Product
import com.example.demo.repository.DiscountPolicyRepository
import com.example.demo.repository.ProductRepository
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

class CartWithDiscountServiceTest {
    
    private lateinit var productRepository: ProductRepository
    private lateinit var discountPolicyRepository: DiscountPolicyRepository
    private lateinit var cartWithDiscountService: CartWithDiscountService

    @BeforeEach
    fun setUp() {
        productRepository = mockk()
        discountPolicyRepository = mockk()
        cartWithDiscountService = CartWithDiscountService(productRepository, discountPolicyRepository)
    }

    @Test
    @DisplayName("할인 없는 장바구니 총액 계산 - 정상 케이스")
    fun `calculateCartTotal should return correct total when no discount policies exist`() {
        // Given
        val product = Product(id = 1L, name = "상품1", price = BigDecimal("10000"), stock = 10)
        val cartRequest = CartRequest(items = listOf(CartItemRequest(productId = 1L, quantity = 2)))
        
        every { productRepository.findById(1L) } returns Optional.of(product)
        every { discountPolicyRepository.findActivePoliciesByDate(any()) } returns emptyList()

        // When
        val result = cartWithDiscountService.calculateCartTotal(cartRequest)

        // Then
        assert(result.items.size == 1)
        assert(result.items[0].productId == 1L)
        assert(result.items[0].productName == "상품1")
        assert(result.items[0].price == BigDecimal("10000"))
        assert(result.items[0].quantity == 2)
        assert(result.items[0].totalPrice == BigDecimal("20000"))
        assert(result.items[0].discountAmount == BigDecimal.ZERO)
        assert(result.items[0].finalPrice == BigDecimal("20000"))
        assert(result.totalAmount == BigDecimal("20000"))
        assert(result.totalItemCount == 2)
        assert(result.totalDiscountAmount == BigDecimal.ZERO)
        assert(result.finalTotalAmount == BigDecimal("20000"))
    }

    @Test
    @DisplayName("상품별 비율 할인 정책 적용 테스트")
    fun `calculateCartTotal should apply product percentage discount correctly`() {
        // Given
        val product = Product(id = 1L, name = "상품1", price = BigDecimal("10000"), stock = 10)
        val discountPolicy = DiscountPolicy(
            id = 1L,
            name = "상품1 10% 할인",
            discountType = DiscountType.PERCENTAGE,
            discountTarget = DiscountTarget.PRODUCT,
            discountValue = BigDecimal("10"),
            targetProductId = 1L,
            isActive = true
        )
        val cartRequest = CartRequest(items = listOf(CartItemRequest(productId = 1L, quantity = 2)))
        
        every { productRepository.findById(1L) } returns Optional.of(product)
        every { discountPolicyRepository.findActivePoliciesByDate(any()) } returns listOf(discountPolicy)

        // When
        val result = cartWithDiscountService.calculateCartTotal(cartRequest)

        // Then
        assert(result.items.size == 1)
        assert(result.items[0].totalPrice == BigDecimal("20000"))
        assert(result.items[0].discountAmount == BigDecimal("2000.00")) // 10% 할인
        assert(result.items[0].finalPrice == BigDecimal("18000.00"))
        assert(result.totalAmount == BigDecimal("20000"))
        assert(result.totalDiscountAmount == BigDecimal("2000.00"))
        assert(result.finalTotalAmount == BigDecimal("18000.00"))
    }

    @Test
    @DisplayName("상품별 고정 금액 할인 정책 적용 테스트")
    fun `calculateCartTotal should apply product fixed amount discount correctly`() {
        // Given
        val product = Product(id = 1L, name = "상품1", price = BigDecimal("10000"), stock = 10)
        val discountPolicy = DiscountPolicy(
            id = 1L,
            name = "상품1 1000원 할인",
            discountType = DiscountType.FIXED_AMOUNT,
            discountTarget = DiscountTarget.PRODUCT,
            discountValue = BigDecimal("1000"),
            targetProductId = 1L,
            isActive = true
        )
        val cartRequest = CartRequest(items = listOf(CartItemRequest(productId = 1L, quantity = 2)))
        
        every { productRepository.findById(1L) } returns Optional.of(product)
        every { discountPolicyRepository.findActivePoliciesByDate(any()) } returns listOf(discountPolicy)

        // When
        val result = cartWithDiscountService.calculateCartTotal(cartRequest)

        // Then
        assert(result.items.size == 1)
        assert(result.items[0].totalPrice == BigDecimal("20000"))
        assert(result.items[0].discountAmount == BigDecimal("1000"))
        assert(result.items[0].finalPrice == BigDecimal("19000"))
        assert(result.totalAmount == BigDecimal("20000"))
        assert(result.totalDiscountAmount == BigDecimal("1000"))
        assert(result.finalTotalAmount == BigDecimal("19000"))
    }

    @Test
    @DisplayName("수량별 할인 정책 적용 테스트")
    fun `calculateCartTotal should apply quantity discount correctly`() {
        // Given
        val product = Product(id = 1L, name = "상품1", price = BigDecimal("10000"), stock = 10)
        val discountPolicy = DiscountPolicy(
            id = 1L,
            name = "3개 이상 구매시 10% 할인",
            discountType = DiscountType.PERCENTAGE,
            discountTarget = DiscountTarget.QUANTITY,
            discountValue = BigDecimal("10"),
            minQuantity = 3,
            isActive = true
        )
        val cartRequest = CartRequest(items = listOf(CartItemRequest(productId = 1L, quantity = 3)))
        
        every { productRepository.findById(1L) } returns Optional.of(product)
        every { discountPolicyRepository.findActivePoliciesByDate(any()) } returns listOf(discountPolicy)

        // When
        val result = cartWithDiscountService.calculateCartTotal(cartRequest)

        // Then
        assert(result.items.size == 1)
        assert(result.items[0].totalPrice == BigDecimal("30000"))
        assert(result.items[0].discountAmount == BigDecimal("3000.00")) // 10% 할인
        assert(result.items[0].finalPrice == BigDecimal("27000.00"))
        assert(result.totalAmount == BigDecimal("30000"))
        assert(result.totalDiscountAmount == BigDecimal("3000.00"))
        assert(result.finalTotalAmount == BigDecimal("27000.00"))
    }

    @Test
    @DisplayName("주문 금액별 할인 정책 적용 테스트")
    fun `calculateCartTotal should apply order amount discount correctly`() {
        // Given
        val product = Product(id = 1L, name = "상품1", price = BigDecimal("10000"), stock = 10)
        val discountPolicy = DiscountPolicy(
            id = 1L,
            name = "5만원 이상 구매시 5000원 할인",
            discountType = DiscountType.FIXED_AMOUNT,
            discountTarget = DiscountTarget.ORDER_AMOUNT,
            discountValue = BigDecimal("5000"),
            minOrderAmount = BigDecimal("50000"),
            isActive = true
        )
        val cartRequest = CartRequest(items = listOf(CartItemRequest(productId = 1L, quantity = 6)))
        
        every { productRepository.findById(1L) } returns Optional.of(product)
        every { discountPolicyRepository.findActivePoliciesByDate(any()) } returns listOf(discountPolicy)

        // When
        val result = cartWithDiscountService.calculateCartTotal(cartRequest)

        // Then
        assert(result.items.size == 1)
        assert(result.items[0].totalPrice == BigDecimal("60000"))
        assert(result.items[0].discountAmount == BigDecimal.ZERO) // 상품별 할인 없음
        assert(result.items[0].finalPrice == BigDecimal("60000"))
        assert(result.totalAmount == BigDecimal("60000"))
        assert(result.totalDiscountAmount == BigDecimal("5000")) // 주문 금액 할인
        assert(result.finalTotalAmount == BigDecimal("55000"))
    }

    @Test
    @DisplayName("복합 할인 정책 적용 테스트")
    fun `calculateCartTotal should apply multiple discount policies correctly`() {
        // Given
        val product = Product(id = 1L, name = "상품1", price = BigDecimal("10000"), stock = 10)
        val productDiscount = DiscountPolicy(
            id = 1L,
            name = "상품1 10% 할인",
            discountType = DiscountType.PERCENTAGE,
            discountTarget = DiscountTarget.PRODUCT,
            discountValue = BigDecimal("10"),
            targetProductId = 1L,
            isActive = true
        )
        val orderDiscount = DiscountPolicy(
            id = 2L,
            name = "5만원 이상 구매시 5000원 할인",
            discountType = DiscountType.FIXED_AMOUNT,
            discountTarget = DiscountTarget.ORDER_AMOUNT,
            discountValue = BigDecimal("5000"),
            minOrderAmount = BigDecimal("50000"),
            isActive = true
        )
        val cartRequest = CartRequest(items = listOf(CartItemRequest(productId = 1L, quantity = 6)))
        
        every { productRepository.findById(1L) } returns Optional.of(product)
        every { discountPolicyRepository.findActivePoliciesByDate(any()) } returns listOf(productDiscount, orderDiscount)

        // When
        val result = cartWithDiscountService.calculateCartTotal(cartRequest)

        // Then
        assert(result.items.size == 1)
        assert(result.items[0].totalPrice == BigDecimal("60000"))
        assert(result.items[0].discountAmount == BigDecimal("6000.00")) // 상품별 10% 할인
        assert(result.items[0].finalPrice == BigDecimal("54000.00"))
        assert(result.totalAmount == BigDecimal("60000"))
        assert(result.totalDiscountAmount == BigDecimal("11000.00")) // 상품 할인 + 주문 할인
        assert(result.finalTotalAmount == BigDecimal("49000.00"))
    }

    @Test
    @DisplayName("최대 할인 금액 제한 테스트")
    fun `calculateCartTotal should respect max discount amount limit`() {
        // Given
        val product = Product(id = 1L, name = "상품1", price = BigDecimal("10000"), stock = 10)
        val discountPolicy = DiscountPolicy(
            id = 1L,
            name = "상품1 50% 할인 (최대 2000원)",
            discountType = DiscountType.PERCENTAGE,
            discountTarget = DiscountTarget.PRODUCT,
            discountValue = BigDecimal("50"),
            maxDiscountAmount = BigDecimal("2000"),
            targetProductId = 1L,
            isActive = true
        )
        val cartRequest = CartRequest(items = listOf(CartItemRequest(productId = 1L, quantity = 2)))
        
        every { productRepository.findById(1L) } returns Optional.of(product)
        every { discountPolicyRepository.findActivePoliciesByDate(any()) } returns listOf(discountPolicy)

        // When
        val result = cartWithDiscountService.calculateCartTotal(cartRequest)

        // Then
        assert(result.items.size == 1)
        assert(result.items[0].totalPrice == BigDecimal("20000"))
        assert(result.items[0].discountAmount == BigDecimal("2000")) // 최대 할인 금액 제한
        assert(result.items[0].finalPrice == BigDecimal("18000"))
        assert(result.totalAmount == BigDecimal("20000"))
        assert(result.totalDiscountAmount == BigDecimal("2000"))
        assert(result.finalTotalAmount == BigDecimal("18000"))
    }

    @Test
    @DisplayName("존재하지 않는 상품 처리 테스트")
    fun `calculateCartTotal should handle non-existent products`() {
        // Given
        val cartRequest = CartRequest(items = listOf(CartItemRequest(productId = 999L, quantity = 1)))
        
        every { productRepository.findById(999L) } returns Optional.empty()
        every { discountPolicyRepository.findActivePoliciesByDate(any()) } returns emptyList()

        // When
        val result = cartWithDiscountService.calculateCartTotal(cartRequest)

        // Then
        assert(result.items.isEmpty())
        assert(result.totalAmount == BigDecimal.ZERO)
        assert(result.totalItemCount == 0)
        assert(result.totalDiscountAmount == BigDecimal.ZERO)
        assert(result.finalTotalAmount == BigDecimal.ZERO)
    }

    @Test
    @DisplayName("장바구니 검증 - 정상 케이스")
    fun `validateCartItems should return empty list when all items are valid`() {
        // Given
        val product = Product(id = 1L, name = "상품1", price = BigDecimal("10000"), stock = 10)
        val cartRequest = CartRequest(items = listOf(CartItemRequest(productId = 1L, quantity = 2)))
        
        every { productRepository.findById(1L) } returns Optional.of(product)

        // When
        val errors = cartWithDiscountService.validateCartItems(cartRequest)

        // Then
        assert(errors.isEmpty())
    }

    @Test
    @DisplayName("장바구니 검증 - 여러 오류")
    fun `validateCartItems should return multiple errors`() {
        // Given
        val product = Product(id = 1L, name = "상품1", price = BigDecimal("10000"), stock = 5)
        val cartRequest = CartRequest(items = listOf(
            CartItemRequest(productId = 1L, quantity = 10), // 재고 부족
            CartItemRequest(productId = 999L, quantity = 1) // 존재하지 않는 상품
        ))
        
        every { productRepository.findById(1L) } returns Optional.of(product)
        every { productRepository.findById(999L) } returns Optional.empty()

        // When
        val errors = cartWithDiscountService.validateCartItems(cartRequest)

        // Then
        assert(errors.size == 2)
        assert(errors.contains("상품 '상품1'의 재고가 부족합니다. (요청: 10개, 재고: 5개)"))
        assert(errors.contains("상품 ID 999에 해당하는 상품을 찾을 수 없습니다."))
    }

    @Test
    @DisplayName("검증과 함께 장바구니 총액 계산 - 정상 케이스")
    fun `calculateCartTotalWithValidation should return result when validation passes`() {
        // Given
        val product = Product(id = 1L, name = "상품1", price = BigDecimal("10000"), stock = 10)
        val cartRequest = CartRequest(items = listOf(CartItemRequest(productId = 1L, quantity = 2)))
        
        every { productRepository.findById(1L) } returns Optional.of(product)
        every { discountPolicyRepository.findActivePoliciesByDate(any()) } returns emptyList()

        // When
        val result = cartWithDiscountService.calculateCartTotalWithValidation(cartRequest)

        // Then
        assert(result.items.size == 1)
        assert(result.totalAmount == BigDecimal("20000"))
        assert(result.finalTotalAmount == BigDecimal("20000"))
    }

    @Test
    @DisplayName("검증과 함께 장바구니 총액 계산 - 검증 실패")
    fun `calculateCartTotalWithValidation should throw exception when validation fails`() {
        // Given
        val cartRequest = CartRequest(items = listOf(CartItemRequest(productId = 999L, quantity = 1)))
        
        every { productRepository.findById(999L) } returns Optional.empty()

        // When & Then
        val exception = assertThrows<IllegalArgumentException> {
            cartWithDiscountService.calculateCartTotalWithValidation(cartRequest)
        }

        assert(exception.message!!.contains("장바구니 검증 실패"))
    }

    @Test
    @DisplayName("BigDecimal 정밀도 테스트")
    fun `calculateCartTotal should handle BigDecimal precision correctly`() {
        // Given
        val product = Product(id = 1L, name = "상품1", price = BigDecimal("1234.56"), stock = 10)
        val discountPolicy = DiscountPolicy(
            id = 1L,
            name = "상품1 10% 할인",
            discountType = DiscountType.PERCENTAGE,
            discountTarget = DiscountTarget.PRODUCT,
            discountValue = BigDecimal("10"),
            targetProductId = 1L,
            isActive = true
        )
        val cartRequest = CartRequest(items = listOf(CartItemRequest(productId = 1L, quantity = 3)))
        
        every { productRepository.findById(1L) } returns Optional.of(product)
        every { discountPolicyRepository.findActivePoliciesByDate(any()) } returns listOf(discountPolicy)

        // When
        val result = cartWithDiscountService.calculateCartTotal(cartRequest)

        // Then
        assert(result.items[0].totalPrice == BigDecimal("3703.68"))
        assert(result.items[0].discountAmount == BigDecimal("370.3680")) // 정확한 소수점 계산
        assert(result.items[0].finalPrice == BigDecimal("3333.3120"))
        assert(result.totalAmount == BigDecimal("3703.68"))
        assert(result.totalDiscountAmount == BigDecimal("370.3680"))
        assert(result.finalTotalAmount == BigDecimal("3333.3120"))
    }
}
