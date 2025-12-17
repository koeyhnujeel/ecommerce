package com.zunza.ecommerce.domain.customer

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class CustomerTest {
    @Test
    fun register() {
        val customer = Customer.register(1L, name = "홍길동", phone = "01012345678")

        customer.accountId shouldBe 1L
        customer.name shouldBe "홍길동"
        customer.phone shouldBe "01012345678"
    }

    @Test
    fun registerFail() {
        shouldThrow<IllegalArgumentException> { Customer.register(0L, name = "홍길동", phone = "01012345678") }
        shouldThrow<IllegalArgumentException> { Customer.register(1L, name = "홍", phone = "01012345678") }
        shouldThrow<IllegalArgumentException> { Customer.register(1L, name = "홍길동", phone = "0101234567") }
    }
}