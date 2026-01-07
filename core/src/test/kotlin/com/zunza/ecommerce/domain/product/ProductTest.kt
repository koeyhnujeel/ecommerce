package com.zunza.ecommerce.domain.product

import com.zunza.ecommerce.domain.product.dto.RegisterProductImageSpec
import com.zunza.ecommerce.domain.product.dto.RegisterProductOptionSpec
import com.zunza.ecommerce.domain.product.dto.RegisterProductSpec
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class ProductTest {
    @Test
    fun `상품 등록에 성공한다`() {
        val spec = createRegisterProductSpec()

        val product = Product.register(spec)

        product.brandId shouldBe spec.brandId
        product.categoryIds shouldBe spec.categoryIds.toSet()
        product.name shouldBe spec.name
        product.description shouldBe spec.description
        product.basePrice.amount shouldBe spec.basePrice
        product.status shouldBe ProductStatus.ON_SALE
        product.productImages shouldHaveSize 2
        product.productImages[0].url shouldBe spec.images[0].url
        product.productImages[0].type shouldBe ImageType.MAIN
        product.productImages[0].displayOrder shouldBe spec.images[0].displayOrder
        product.productOptions shouldHaveSize 2
        product.productOptions[0].color shouldBe spec.options[0].color
        product.productOptions[0].size shouldBe spec.options[0].size
        product.productOptions[0].additionalPrice.amount shouldBe spec.options[0].additionalPrice
        product.productOptions[0].status shouldBe ProductOptionStatus.ON_SALE
        product.registeredAt shouldNotBe null
        product.updatedAt shouldNotBe null
    }

    @Test
    fun `상품 등록 필수 값이 누락되면 예외가 발생한다`() {
        shouldThrow<IllegalArgumentException> { Product.register(createRegisterProductSpec(brandId = 0L)) }
        shouldThrow<IllegalArgumentException> { Product.register(createRegisterProductSpec(categoryIds = emptyList())) }
        shouldThrow<IllegalArgumentException> { Product.register(createRegisterProductSpec(name = "")) }
        shouldThrow<IllegalArgumentException> { Product.register(createRegisterProductSpec(description = "")) }
        shouldThrow<IllegalArgumentException> {
            Product.register(createRegisterProductSpec(basePrice = BigDecimal("99")))
        }
        shouldThrow<IllegalArgumentException> { Product.register(createRegisterProductSpec(images = emptyList())) }
        shouldThrow<IllegalArgumentException> { Product.register(createRegisterProductSpec(options = emptyList())) }
    }

    @Test
    fun `상품 이미지 타입이 올바르지 않으면 예외가 발생한다`() {
        val spec = createRegisterProductSpec(
            images = listOf(
                RegisterProductImageSpec(
                    url = "https://cdn.example.com/images/invalid.png",
                    type = "INVALID",
                    displayOrder = 1
                )
            )
        )

        shouldThrow<InvalidImageRoleException> {
            Product.register(spec)
        }
    }

    @Test
    fun `상품 옵션 값이 유효하지 않으면 예외가 발생한다`() {
        val invalidColorSpec = createRegisterProductSpec(
            options = listOf(
                RegisterProductOptionSpec(
                    color = "",
                    size = "M",
                    additionalPrice = BigDecimal("100")
                )
            )
        )

        val invalidSizeSpec = createRegisterProductSpec(
            options = listOf(
                RegisterProductOptionSpec(
                    color = "RED",
                    size = "",
                    additionalPrice = BigDecimal("100")
                )
            )
        )

        shouldThrow<IllegalArgumentException> { Product.register(invalidColorSpec) }
        shouldThrow<IllegalArgumentException> { Product.register(invalidSizeSpec) }
    }

    private fun createRegisterProductSpec(
        brandId: Long = 1L,
        categoryIds: List<Long> = listOf(10L, 20L),
        name: String = "베이직 티셔츠",
        description: String = "데일리 착용에 적합한 티셔츠입니다.",
        basePrice: BigDecimal = BigDecimal("1000"),
        images: List<RegisterProductImageSpec> = listOf(
            RegisterProductImageSpec(
                url = "https://cdn.example.com/images/main.png",
                type = "MAIN",
                displayOrder = 1
            ),
            RegisterProductImageSpec(
                url = "https://cdn.example.com/images/detail.png",
                type = "DETAIL",
                displayOrder = 2
            )
        ),
        options: List<RegisterProductOptionSpec> = listOf(
            RegisterProductOptionSpec(
                color = "RED",
                size = "M",
                additionalPrice = BigDecimal("100")
            ),
            RegisterProductOptionSpec(
                color = "BLUE",
                size = "L",
                additionalPrice = BigDecimal("200")
            )
        )
    ) = RegisterProductSpec(
        brandId = brandId,
        categoryIds = categoryIds,
        name = name,
        description = description,
        basePrice = basePrice,
        images = images,
        options = options
    )
}
