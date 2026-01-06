package com.zunza.ecommerce.application.product.provided

import com.zunza.ecommerce.application.product.required.ProductRepository
import com.zunza.ecommerce.application.product.service.ProductRegistrationService
import com.zunza.ecommerce.application.product.service.dto.command.RegisterProductCommand
import com.zunza.ecommerce.application.product.service.dto.command.RegisterProductImageCommand
import com.zunza.ecommerce.application.product.service.dto.command.RegisterProductOptionCommand
import com.zunza.ecommerce.application.stock.provided.StockRegister
import com.zunza.ecommerce.application.stock.service.command.RegisterStockCommand
import com.zunza.ecommerce.domain.prodcut.Product
import com.zunza.ecommerce.domain.prodcut.ProductOption
import io.kotest.matchers.shouldBe
import io.mockk.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class ProductRegisterTest {
    private lateinit var productRepository: ProductRepository
    private lateinit var stockRegister: StockRegister
    private lateinit var productRegister: ProductRegister

    @BeforeEach
    fun setUp() {
        productRepository = mockk()
        stockRegister = mockk()
        productRegister = ProductRegistrationService(productRepository, stockRegister)
    }

    @AfterEach
    fun cleanUp() {
        clearAllMocks()
        unmockkAll()
    }

    @Test
    fun `상품 등록 시 상품과 재고를 등록한다`() {
        val command = createRegisterProductCommand()
        val firstOption = mockk<ProductOption>()
        val secondOption = mockk<ProductOption>()
        val productOptions = mutableListOf(firstOption, secondOption)
        val savedProduct = mockk<Product>()
        val productId = 555L

        every { firstOption.id } returns 101L
        every { firstOption.color } returns "RED"
        every { firstOption.size } returns "M"
        every { secondOption.id } returns 202L
        every { secondOption.color } returns "BLACK"
        every { secondOption.size } returns "L"
        every { savedProduct.id } returns productId
        every { savedProduct.productOptions } returns productOptions

        mockkObject(Product.Companion)
        every { Product.register(command.toSpec()) } returns savedProduct
        every { productRepository.save(savedProduct) } returns savedProduct
        every { stockRegister.register(any()) } just Runs

        val result = productRegister.register(command)

        result shouldBe productId

        verify(exactly = 1) {
            Product.register(command.toSpec())
            productRepository.save(savedProduct)
            stockRegister.register(RegisterStockCommand(101L, 10))
            stockRegister.register(RegisterStockCommand(202L, 5))
        }
    }

    private fun createRegisterProductCommand() =
        RegisterProductCommand(
            brandId = 1,
            categoryIds = listOf(10L, 20L),
            name = "티셔츠",
            description = "부드러운 소재",
            basePrice = BigDecimal("1500"),
            images = listOf(
                RegisterProductImageCommand(
                    url = "https://example.com/main.png",
                    type = "MAIN",
                    displayOrder = 1
                )
            ),
            options = listOf(
                RegisterProductOptionCommand(
                    color = "RED",
                    size = "M",
                    additionalPrice = BigDecimal.ZERO,
                    quantity = 10
                ),
                RegisterProductOptionCommand(
                    color = "BLACK",
                    size = "L",
                    additionalPrice = BigDecimal.ZERO,
                    quantity = 5
                )
            )
        )
}
