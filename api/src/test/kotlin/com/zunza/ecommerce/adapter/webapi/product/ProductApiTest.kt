package com.zunza.ecommerce.adapter.webapi.product

import com.fasterxml.jackson.databind.ObjectMapper
import com.zunza.ecommerce.adapter.persistence.product.ProductJpaRepository
import com.zunza.ecommerce.adapter.persistence.stock.StockJpaRepository
import com.zunza.ecommerce.adapter.webapi.product.dto.request.RegisterProductImageRequest
import com.zunza.ecommerce.adapter.webapi.product.dto.request.RegisterProductOptionRequest
import com.zunza.ecommerce.adapter.webapi.product.dto.request.RegisterProductRequest
import com.zunza.ecommerce.config.TestConfiguration
import com.zunza.ecommerce.config.TestContainersConfiguration
import com.zunza.ecommerce.domain.product.ProductOptionStatus
import com.zunza.ecommerce.domain.product.ProductStatus
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Transactional
@AutoConfigureMockMvc
@ActiveProfiles("test")
@SpringBootTest(classes = [TestConfiguration::class, TestContainersConfiguration::class])
class ProductApiTest(
    private val mockMvc: MockMvc,
    private val objectMapper: ObjectMapper,
    private val productJpaRepository: ProductJpaRepository,
    private val stockJpaRepository: StockJpaRepository
) {
    @Test
    @WithMockUser(authorities = ["ROLE_SELLER"])
    fun `상품 등록 시 상품과 재고가 저장된다`() {
        val request = RegisterProductRequest(
            brandId = 1L,
            categoryIds = listOf(10L, 20L),
            name = "티셔츠",
            description = "부드러운 소재",
            basePrice = BigDecimal("1500"),
            images = listOf(
                RegisterProductImageRequest(
                    url = "https://example.com/main.png",
                    type = "MAIN",
                    displayOrder = 1
                )
            ),
            options = listOf(
                RegisterProductOptionRequest(
                    color = "RED",
                    size = "M",
                    additionalPrice = BigDecimal.ZERO,
                    quantity = 10
                ),
                RegisterProductOptionRequest(
                    color = "BLACK",
                    size = "L",
                    additionalPrice = BigDecimal("500"),
                    quantity = 5
                )
            )
        )

        val result = mockMvc.post("/api/products") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
        }.andExpect {
            status { isCreated() }
            jsonPath("$.success") { value(true) }
            jsonPath("$.data.productId") { exists() }
            jsonPath("$.timestamp") { exists() }
        }.andReturn()

        val responseJson = objectMapper.readTree(result.response.contentAsString)
        val productId = responseJson["data"]["productId"].asLong()

        productId shouldNotBe 0L

        val product = productJpaRepository.findById(productId).orElseThrow()

        product.brandId shouldBe request.brandId
        product.categoryIds shouldBe request.categoryIds.toSet()
        product.name shouldBe request.name
        product.description shouldBe request.description
        product.basePrice.amount shouldBe request.basePrice
        product.status shouldBe ProductStatus.ON_SALE
        product.productImages shouldHaveSize 1
        product.productImages[0].url shouldBe request.images[0].url
        product.productImages[0].displayOrder shouldBe request.images[0].displayOrder
        product.productOptions shouldHaveSize 2

        val optionMap = product.productOptions.associateBy { it.color to it.size }
        val firstOption = optionMap[request.options[0].color to request.options[0].size]
        val secondOption = optionMap[request.options[1].color to request.options[1].size]

        firstOption shouldNotBe null
        secondOption shouldNotBe null
        firstOption!!.additionalPrice.amount shouldBe request.options[0].additionalPrice
        firstOption.status shouldBe ProductOptionStatus.ON_SALE
        secondOption!!.additionalPrice.amount shouldBe request.options[1].additionalPrice
        secondOption.status shouldBe ProductOptionStatus.ON_SALE

        val stocks = stockJpaRepository.findAll()

        stocks shouldHaveSize 2

        val firstStock = stocks.find { it.productOptionId == firstOption!!.id }
        val secondStock = stocks.find { it.productOptionId == secondOption!!.id }

        firstStock shouldNotBe null
        secondStock shouldNotBe null
        firstStock!!.quantity shouldBe request.options[0].quantity
        secondStock!!.quantity shouldBe request.options[1].quantity
    }
}
