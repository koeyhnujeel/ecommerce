package com.zunza.ecommerce.domain.brand

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class BrandInfoTest {
    @Test
    fun brandInfo() {
        val brandInfo = BrandInfo("나이키", "Nike", "스포츠 의류 전문")

        brandInfo.nameKor shouldBe "나이키"
        brandInfo.nameEng shouldBe "Nike"
        brandInfo.description shouldBe "스포츠 의류 전문"
    }

    @Test
    fun brandInfoFail() {
        shouldThrow<IllegalArgumentException> {
            BrandInfo("", "Nike", "스포츠 의류 전문")
        }.message shouldBe "브랜드 이름(국문)은 필수입니다."

        shouldThrow<IllegalArgumentException> {
            BrandInfo("나이키", "", "스포츠 의류 전문")
        }.message shouldBe "브랜드 이름(영문)은 필수입니다."

        shouldThrow<IllegalArgumentException> {
            BrandInfo("나이키", "Nike", "")
        }.message shouldBe "브랜드 소개는 필수입니다."
    }
}