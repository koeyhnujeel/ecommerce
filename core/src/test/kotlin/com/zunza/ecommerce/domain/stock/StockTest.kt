package com.zunza.ecommerce.domain.stock

import com.zunza.ecommerce.domain.stock.dto.RegisterStockSpec
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class StockTest {
    @Test
    fun `재고 등록에 성공한다`() {
        val spec = createRegisterStockSpec()

        val stock = Stock.register(spec)

        stock.productOptionId shouldBe spec.productOptionsId
        stock.quantity shouldBe spec.quantity
    }

    @Test
    fun `상품 옵션 아이디가 유효하지 않으면 예외가 발생한다`() {
        shouldThrow<IllegalArgumentException> {
            Stock.register(createRegisterStockSpec(productOptionId = 0L))
        }.message shouldBe "상품 옵션 ID는 0 이상이어야 합니다."
    }

    @Test
    fun `재고 수량이 유효하지 않으면 예외가 발생한다`() {
        shouldThrow<IllegalArgumentException> {
            Stock.register(createRegisterStockSpec(quantity = 0))
        }.message shouldBe "재고 수량은 0개 이상이어야 합니다."
    }

    private fun createRegisterStockSpec(
        productOptionId: Long = 1L,
        quantity: Int = 10
    ) = RegisterStockSpec(
        productOptionsId = productOptionId,
        quantity = quantity
    )
}
