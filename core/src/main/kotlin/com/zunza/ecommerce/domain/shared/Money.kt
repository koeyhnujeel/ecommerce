package com.zunza.ecommerce.domain.shared

import jakarta.persistence.Embeddable
import java.math.BigDecimal

@Embeddable
data class Money(
    val amount: BigDecimal
) {
    init {
        require(amount >= BigDecimal.ZERO) { "금액은 음수일 수 없습니다." }
    }
}
