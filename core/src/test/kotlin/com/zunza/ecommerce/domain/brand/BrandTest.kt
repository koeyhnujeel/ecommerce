package com.zunza.ecommerce.domain.brand

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class BrandTest {
    @Test
    fun register() {
        val brand = Brand.register(
            1L,
            BrandInfo("나이키", "Nike", "스포츠 의류 전문")
        )

        brand.sellerId shouldBe 1L
        brand.brandInfo.nameKor shouldBe "나이키"
        brand.brandInfo.nameEng shouldBe "Nike"
        brand.brandInfo.description shouldBe "스포츠 의류 전문"
    }
}