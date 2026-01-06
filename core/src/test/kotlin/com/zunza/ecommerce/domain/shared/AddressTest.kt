package com.zunza.ecommerce.shared

import com.zunza.ecommerce.domain.shared.Address
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class AddressTest {
    @Test
    fun address() {
        val address = Address(
            "서울특별시 관악구 관악로 1",
            "",
            "11111"
        )

        address.roadAddress shouldBe "서울특별시 관악구 관악로 1"
        address.detailAddress shouldBe ""
        address.zipcode shouldBe "11111"
    }

    @Test
    fun addressFail() {
        shouldThrow<IllegalArgumentException> {
            Address("", "", "11111")
        }.message shouldBe "도로명 주소는 필수입니다."

        shouldThrow<IllegalArgumentException> {
            Address("서울특별시 관악구 관악로 1", "", "1111")
        }.message shouldBe "우편번호 형식이 올바르지 않습니다."
    }
}