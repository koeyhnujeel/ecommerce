package com.zunza.ecommerce.domain.seller

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class BusinessInfoTest {
    @Test
    fun businessInfo() {
        val businessInfo = BusinessInfo("1234567899", "컴퍼니")

        businessInfo.businessNumber shouldBe "1234567899"
        businessInfo.companyName shouldBe "컴퍼니"
    }

    @Test
    fun businessInfoFail() {
        shouldThrow<IllegalArgumentException> {
            BusinessInfo("123456789", "컴퍼니")
        }.message shouldBe "사업자등록번호 형식이 올바르지 않습니다."

        shouldThrow<IllegalArgumentException> {
            BusinessInfo("1234567899", "")
        }.message shouldBe "업체명은 필수입니다."
    }
}