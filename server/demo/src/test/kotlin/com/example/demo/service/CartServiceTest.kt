package com.example.demo.service

import com.example.demo.dto.CartItemRequest
import com.example.demo.dto.CartRequest
import com.example.demo.entity.Product
import com.example.demo.repository.ProductRepository
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal
import java.util.*

class CartServiceTest {

    private lateinit var productRepository: ProductRepository
    private lateinit var cartService: CartService

    @BeforeEach
    fun setUp() {
        productRepository = mockk()
        cartService = CartService(productRepository)
    }

    @Test
    @DisplayName("장바구니 총액 계산 - 정상 케이스")
    fun `calculateCartTotal should return correct total when products exist`() {
        // Given
        val product1 = Product(
            id = 1L,
            name = "상품1",
            price = BigDecimal("10000"),
            stock = 10,
            isActive = true
        )
        val product2 = Product(
            id = 2L,
            name = "상품2",
            price = BigDecimal("20000"),
            stock = 5,
            isActive = true
        )

        val cartRequest = CartRequest(
            items = listOf(
                CartItemRequest(productId = 1L, quantity = 2),
                CartItemRequest(productId = 2L, quantity = 1)
            )
        )

        every { productRepository.findById(1L) } returns Optional.of(product1)
        every { productRepository.findById(2L) } returns Optional.of(product2)

        // When
        val result = cartService.calculateCartTotal(cartRequest)

        // Then
        assert(result.items.size == 2)
        assert(result.totalAmount == BigDecimal("40000")) // 10000*2 + 20000*1
        assert(result.totalItemCount == 3) // 2 + 1
        assert(result.items[0].totalPrice == BigDecimal("20000"))
        assert(result.items[1].totalPrice == BigDecimal("20000"))
    }

    @Test
    @DisplayName("장바구니 총액 계산 - 존재하지 않는 상품 제외")
    fun `calculateCartTotal should exclude non-existent products`() {
        // Given
        val product1 = Product(
            id = 1L,
            name = "상품1",
            price = BigDecimal("10000"),
            stock = 10,
            isActive = true
        )

        val cartRequest = CartRequest(
            items = listOf(
                CartItemRequest(productId = 1L, quantity = 2),
                CartItemRequest(productId = 999L, quantity = 1) // 존재하지 않는 상품
            )
        )

        every { productRepository.findById(1L) } returns Optional.of(product1)
        every { productRepository.findById(999L) } returns Optional.empty()

        // When
        val result = cartService.calculateCartTotal(cartRequest)

        // Then
        assert(result.items.size == 1)
        assert(result.totalAmount == BigDecimal("20000"))
        assert(result.totalItemCount == 2)
    }

    @Test
    @DisplayName("장바구니 총액 계산 - 비활성 상품 제외")
    fun `calculateCartTotal should exclude inactive products`() {
        // Given
        val product1 = Product(
            id = 1L,
            name = "상품1",
            price = BigDecimal("10000"),
            stock = 10,
            isActive = true
        )
        val inactiveProduct = Product(
            id = 2L,
            name = "비활성상품",
            price = BigDecimal("20000"),
            stock = 5,
            isActive = false
        )

        val cartRequest = CartRequest(
            items = listOf(
                CartItemRequest(productId = 1L, quantity = 2),
                CartItemRequest(productId = 2L, quantity = 1)
            )
        )

        every { productRepository.findById(1L) } returns Optional.of(product1)
        every { productRepository.findById(2L) } returns Optional.of(inactiveProduct)

        // When
        val result = cartService.calculateCartTotal(cartRequest)

        // Then
        assert(result.items.size == 1)
        assert(result.totalAmount == BigDecimal("20000"))
        assert(result.totalItemCount == 2)
    }

    @Test
    @DisplayName("장바구니 검증 - 정상 케이스")
    fun `validateCartItems should return empty list when all validations pass`() {
        // Given
        val product1 = Product(
            id = 1L,
            name = "상품1",
            price = BigDecimal("10000"),
            stock = 10,
            isActive = true
        )
        val product2 = Product(
            id = 2L,
            name = "상품2",
            price = BigDecimal("20000"),
            stock = 5,
            isActive = true
        )

        val cartRequest = CartRequest(
            items = listOf(
                CartItemRequest(productId = 1L, quantity = 2),
                CartItemRequest(productId = 2L, quantity = 1)
            )
        )

        every { productRepository.findById(1L) } returns Optional.of(product1)
        every { productRepository.findById(2L) } returns Optional.of(product2)

        // When
        val errors = cartService.validateCartItems(cartRequest)

        // Then
        assert(errors.isEmpty())
    }

    @Test
    @DisplayName("장바구니 검증 - 존재하지 않는 상품")
    fun `validateCartItems should return error for non-existent product`() {
        // Given
        val cartRequest = CartRequest(
            items = listOf(
                CartItemRequest(productId = 999L, quantity = 1)
            )
        )

        every { productRepository.findById(999L) } returns Optional.empty()

        // When
        val errors = cartService.validateCartItems(cartRequest)

        // Then
        assert(errors.size == 1)
        assert(errors[0] == "상품 ID 999에 해당하는 상품을 찾을 수 없습니다.")
    }

    @Test
    @DisplayName("장바구니 검증 - 비활성 상품")
    fun `validateCartItems should return error for inactive product`() {
        // Given
        val inactiveProduct = Product(
            id = 1L,
            name = "비활성상품",
            price = BigDecimal("10000"),
            stock = 10,
            isActive = false
        )

        val cartRequest = CartRequest(
            items = listOf(
                CartItemRequest(productId = 1L, quantity = 1)
            )
        )

        every { productRepository.findById(1L) } returns Optional.of(inactiveProduct)

        // When
        val errors = cartService.validateCartItems(cartRequest)

        // Then
        assert(errors.size == 1)
        assert(errors[0] == "상품 ID 1에 해당하는 상품을 찾을 수 없습니다.")
    }

    @Test
    @DisplayName("장바구니 검증 - 수량이 0 이하인 경우")
    fun `validateCartItems should return error for zero or negative quantity`() {
        // Given
        val product = Product(
            id = 1L,
            name = "상품1",
            price = BigDecimal("10000"),
            stock = 10,
            isActive = true
        )

        val cartRequest = CartRequest(
            items = listOf(
                CartItemRequest(productId = 1L, quantity = 0)
            )
        )

        every { productRepository.findById(1L) } returns Optional.of(product)

        // When
        val errors = cartService.validateCartItems(cartRequest)

        // Then
        assert(errors.size == 1)
        assert(errors[0] == "상품 '상품1'의 수량은 1개 이상이어야 합니다.")
    }

    @Test
    @DisplayName("장바구니 검증 - 재고 부족")
    fun `validateCartItems should return error for insufficient stock`() {
        // Given
        val product = Product(
            id = 1L,
            name = "상품1",
            price = BigDecimal("10000"),
            stock = 5,
            isActive = true
        )

        val cartRequest = CartRequest(
            items = listOf(
                CartItemRequest(productId = 1L, quantity = 10)
            )
        )

        every { productRepository.findById(1L) } returns Optional.of(product)

        // When
        val errors = cartService.validateCartItems(cartRequest)

        // Then
        assert(errors.size == 1)
        assert(errors[0] == "상품 '상품1'의 재고가 부족합니다. (요청: 10개, 재고: 5개)")
    }

    @Test
    @DisplayName("장바구니 검증 - 여러 오류")
    fun `validateCartItems should return multiple errors`() {
        // Given
        val product1 = Product(
            id = 1L,
            name = "상품1",
            price = BigDecimal("10000"),
            stock = 5,
            isActive = true
        )

        val cartRequest = CartRequest(
            items = listOf(
                CartItemRequest(productId = 1L, quantity = 10), // 재고 부족
                CartItemRequest(productId = 999L, quantity = 1) // 존재하지 않는 상품
            )
        )

        every { productRepository.findById(1L) } returns Optional.of(product1)
        every { productRepository.findById(999L) } returns Optional.empty()

        // When
        val errors = cartService.validateCartItems(cartRequest)

        // Then
        assert(errors.size == 2)
        assert(errors.contains("상품 '상품1'의 재고가 부족합니다. (요청: 10개, 재고: 5개)"))
        assert(errors.contains("상품 ID 999에 해당하는 상품을 찾을 수 없습니다."))
    }

    @Test
    @DisplayName("검증과 함께 장바구니 총액 계산 - 정상 케이스")
    fun `calculateCartTotalWithValidation should return correct total when validation passes`() {
        // Given
        val product1 = Product(
            id = 1L,
            name = "상품1",
            price = BigDecimal("10000"),
            stock = 10,
            isActive = true
        )

        val cartRequest = CartRequest(
            items = listOf(
                CartItemRequest(productId = 1L, quantity = 2)
            )
        )

        every { productRepository.findById(1L) } returns Optional.of(product1)

        // When
        val result = cartService.calculateCartTotalWithValidation(cartRequest)

        // Then
        assert(result.items.size == 1)
        assert(result.totalAmount == BigDecimal("20000"))
        assert(result.totalItemCount == 2)
    }

    @Test
    @DisplayName("검증과 함께 장바구니 총액 계산 - 검증 실패")
    fun `calculateCartTotalWithValidation should throw exception when validation fails`() {
        // Given
        val cartRequest = CartRequest(
            items = listOf(
                CartItemRequest(productId = 999L, quantity = 1)
            )
        )

        every { productRepository.findById(999L) } returns Optional.empty()

        // When & Then
        val exception = assertThrows<IllegalArgumentException> {
            cartService.calculateCartTotalWithValidation(cartRequest)
        }

        assert(exception.message!!.contains("장바구니 검증 실패"))
        assert(exception.message!!.contains("상품 ID 999에 해당하는 상품을 찾을 수 없습니다"))
    }

    @Test
    @DisplayName("빈 장바구니 처리")
    fun `calculateCartTotal should handle empty cart`() {
        // Given
        val cartRequest = CartRequest(items = emptyList())

        // When
        val result = cartService.calculateCartTotal(cartRequest)

        // Then
        assert(result.items.isEmpty())
        assert(result.totalAmount == BigDecimal.ZERO)
        assert(result.totalItemCount == 0)
    }

    @Test
    @DisplayName("빈 장바구니 검증")
    fun `validateCartItems should handle empty cart`() {
        // Given
        val cartRequest = CartRequest(items = emptyList())

        // When
        val errors = cartService.validateCartItems(cartRequest)

        // Then
        assert(errors.isEmpty())
    }

    @Test
    @DisplayName("BigDecimal 정밀도 테스트")
    fun `calculateCartTotal should handle BigDecimal precision correctly`() {
        // Given
        val product = Product(
            id = 1L,
            name = "소수점 상품",
            price = BigDecimal("1234.56"),
            stock = 100,
            isActive = true
        )

        val cartRequest = CartRequest(
            items = listOf(
                CartItemRequest(productId = 1L, quantity = 3)
            )
        )

        every { productRepository.findById(1L) } returns Optional.of(product)

        // When
        val result = cartService.calculateCartTotal(cartRequest)

        // Then
        assert(result.totalAmount == BigDecimal("3703.68")) // 1234.56 * 3
        assert(result.items[0].totalPrice == BigDecimal("3703.68"))
    }

    @Test
    @DisplayName("대량 상품 처리 테스트")
    fun `calculateCartTotal should handle large quantities`() {
        // Given
        val product = Product(
            id = 1L,
            name = "대량 상품",
            price = BigDecimal("100"),
            stock = 10000,
            isActive = true
        )

        val cartRequest = CartRequest(
            items = listOf(
                CartItemRequest(productId = 1L, quantity = 1000)
            )
        )

        every { productRepository.findById(1L) } returns Optional.of(product)

        // When
        val result = cartService.calculateCartTotal(cartRequest)

        // Then
        assert(result.totalAmount == BigDecimal("100000")) // 100 * 1000
        assert(result.totalItemCount == 1000)
    }

    @Test
    @DisplayName("음수 수량 검증 테스트")
    fun `validateCartItems should return error for negative quantity`() {
        // Given
        val product = Product(
            id = 1L,
            name = "상품1",
            price = BigDecimal("10000"),
            stock = 10,
            isActive = true
        )

        val cartRequest = CartRequest(
            items = listOf(
                CartItemRequest(productId = 1L, quantity = -1)
            )
        )

        every { productRepository.findById(1L) } returns Optional.of(product)

        // When
        val errors = cartService.validateCartItems(cartRequest)

        // Then
        assert(errors.size == 1)
        assert(errors[0] == "상품 '상품1'의 수량은 1개 이상이어야 합니다.")
    }

    @Test
    @DisplayName("음수 수량 계산 테스트 - 음수 수량도 계산에 포함됨")
    fun `calculateCartTotal should include negative quantity items`() {
        // Given
        val product = Product(
            id = 1L,
            name = "상품1",
            price = BigDecimal("10000"),
            stock = 10,
            isActive = true
        )

        val cartRequest = CartRequest(
            items = listOf(
                CartItemRequest(productId = 1L, quantity = -1)
            )
        )

        every { productRepository.findById(1L) } returns Optional.of(product)

        // When
        val result = cartService.calculateCartTotal(cartRequest)

        // Then
        assert(result.items.size == 1)
        assert(result.totalAmount == BigDecimal("-10000")) // 10000 * (-1)
        assert(result.totalItemCount == -1)
    }

    @Test
    @DisplayName("0 수량 계산 테스트 - 0 수량도 계산에 포함됨")
    fun `calculateCartTotal should include zero quantity items`() {
        // Given
        val product = Product(
            id = 1L,
            name = "상품1",
            price = BigDecimal("10000"),
            stock = 10,
            isActive = true
        )

        val cartRequest = CartRequest(
            items = listOf(
                CartItemRequest(productId = 1L, quantity = 0)
            )
        )

        every { productRepository.findById(1L) } returns Optional.of(product)

        // When
        val result = cartService.calculateCartTotal(cartRequest)

        // Then
        assert(result.items.size == 1)
        assert(result.totalAmount == BigDecimal.ZERO) // 10000 * 0
        assert(result.totalItemCount == 0)
    }

    @Test
    @DisplayName("경계값 테스트 - Integer.MAX_VALUE 수량")
    fun `validateCartItems should handle Integer MAX VALUE quantity`() {
        // Given
        val product = Product(
            id = 1L,
            name = "상품1",
            price = BigDecimal("10000"),
            stock = Int.MAX_VALUE,
            isActive = true
        )

        val cartRequest = CartRequest(
            items = listOf(
                CartItemRequest(productId = 1L, quantity = Int.MAX_VALUE)
            )
        )

        every { productRepository.findById(1L) } returns Optional.of(product)

        // When
        val errors = cartService.validateCartItems(cartRequest)

        // Then
        assert(errors.isEmpty()) // 재고가 충분하므로 오류 없음
    }

    @Test
    @DisplayName("경계값 테스트 - Integer.MAX_VALUE 수량 계산")
    fun `calculateCartTotal should handle Integer MAX VALUE quantity`() {
        // Given
        val product = Product(
            id = 1L,
            name = "상품1",
            price = BigDecimal("10000"),
            stock = Int.MAX_VALUE,
            isActive = true
        )

        val cartRequest = CartRequest(
            items = listOf(
                CartItemRequest(productId = 1L, quantity = Int.MAX_VALUE)
            )
        )

        every { productRepository.findById(1L) } returns Optional.of(product)

        // When
        val result = cartService.calculateCartTotal(cartRequest)

        // Then
        val expectedTotal = BigDecimal("10000").multiply(BigDecimal(Int.MAX_VALUE))
        assert(result.items.size == 1)
        assert(result.totalAmount == expectedTotal)
        assert(result.totalItemCount == Int.MAX_VALUE)
    }

    @Test
    @DisplayName("경계값 테스트 - 재고보다 많은 수량")
    fun `validateCartItems should return error for quantity exceeding stock`() {
        // Given
        val product = Product(
            id = 1L,
            name = "상품1",
            price = BigDecimal("10000"),
            stock = 100,
            isActive = true
        )

        val cartRequest = CartRequest(
            items = listOf(
                CartItemRequest(productId = 1L, quantity = 101) // 재고보다 1개 많은 수량
            )
        )

        every { productRepository.findById(1L) } returns Optional.of(product)

        // When
        val errors = cartService.validateCartItems(cartRequest)

        // Then
        assert(errors.size == 1)
        assert(errors[0] == "상품 '상품1'의 재고가 부족합니다. (요청: 101개, 재고: 100개)")
    }

    @Test
    @DisplayName("null 안전성 테스트 - null 상품 ID")
    fun `validateCartItems should handle null product ID gracefully`() {
        // Given
        val cartRequest = CartRequest(
            items = listOf(
                CartItemRequest(productId = 0L, quantity = 1) // 0은 null과 유사한 상황
            )
        )

        every { productRepository.findById(0L) } returns Optional.empty()

        // When
        val errors = cartService.validateCartItems(cartRequest)

        // Then
        assert(errors.size == 1)
        assert(errors[0] == "상품 ID 0에 해당하는 상품을 찾을 수 없습니다.")
    }

    @Test
    @DisplayName("null 안전성 테스트 - 빈 장바구니 아이템 리스트")
    fun `validateCartItems should handle empty items list`() {
        // Given
        val cartRequest = CartRequest(items = emptyList())

        // When
        val errors = cartService.validateCartItems(cartRequest)

        // Then
        assert(errors.isEmpty())
    }

    @Test
    @DisplayName("null 안전성 테스트 - null 가격 상품")
    fun `calculateCartTotal should handle null price gracefully`() {
        // Given
        val product = Product(
            id = 1L,
            name = "상품1",
            price = BigDecimal.ZERO, // 0 가격
            stock = 10,
            isActive = true
        )

        val cartRequest = CartRequest(
            items = listOf(
                CartItemRequest(productId = 1L, quantity = 1)
            )
        )

        every { productRepository.findById(1L) } returns Optional.of(product)

        // When
        val result = cartService.calculateCartTotal(cartRequest)

        // Then
        assert(result.items.size == 1)
        assert(result.totalAmount == BigDecimal.ZERO)
        assert(result.totalItemCount == 1)
    }

    @Test
    @DisplayName("경계값 테스트 - 최소 수량 (1)")
    fun `validateCartItems should pass for minimum valid quantity`() {
        // Given
        val product = Product(
            id = 1L,
            name = "상품1",
            price = BigDecimal("10000"),
            stock = 1,
            isActive = true
        )

        val cartRequest = CartRequest(
            items = listOf(
                CartItemRequest(productId = 1L, quantity = 1) // 최소 유효 수량
            )
        )

        every { productRepository.findById(1L) } returns Optional.of(product)

        // When
        val errors = cartService.validateCartItems(cartRequest)

        // Then
        assert(errors.isEmpty())
    }

    @Test
    @DisplayName("경계값 테스트 - 재고와 동일한 수량")
    fun `validateCartItems should pass for quantity equal to stock`() {
        // Given
        val product = Product(
            id = 1L,
            name = "상품1",
            price = BigDecimal("10000"),
            stock = 5,
            isActive = true
        )

        val cartRequest = CartRequest(
            items = listOf(
                CartItemRequest(productId = 1L, quantity = 5) // 재고와 동일한 수량
            )
        )

        every { productRepository.findById(1L) } returns Optional.of(product)

        // When
        val errors = cartService.validateCartItems(cartRequest)

        // Then
        assert(errors.isEmpty())
    }

    @Test
    @DisplayName("경계값 테스트 - 재고보다 1개 많은 수량")
    fun `validateCartItems should return error for quantity exceeding stock by 1`() {
        // Given
        val product = Product(
            id = 1L,
            name = "상품1",
            price = BigDecimal("10000"),
            stock = 5,
            isActive = true
        )

        val cartRequest = CartRequest(
            items = listOf(
                CartItemRequest(productId = 1L, quantity = 6) // 재고보다 1개 많은 수량
            )
        )

        every { productRepository.findById(1L) } returns Optional.of(product)

        // When
        val errors = cartService.validateCartItems(cartRequest)

        // Then
        assert(errors.size == 1)
        assert(errors[0] == "상품 '상품1'의 재고가 부족합니다. (요청: 6개, 재고: 5개)")
    }
}