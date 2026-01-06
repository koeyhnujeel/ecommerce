package com.zunza.ecommerce.domain.shared

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class MoneyTest {
    @Test
    fun `금액이 양수이면 생성에 성공한다`() {
        val money = Money(BigDecimal("1000"))

        money.amount shouldBe BigDecimal("1000")
    }

    @Test
    fun `금액이 0 이하이면 예외가 발생한다`() {
        shouldThrow<IllegalArgumentException> { Money(BigDecimal.ZERO) }
        shouldThrow<IllegalArgumentException> { Money(BigDecimal("-1")) }
    }
}
