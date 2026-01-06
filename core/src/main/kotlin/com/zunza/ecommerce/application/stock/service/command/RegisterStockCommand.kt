package com.zunza.ecommerce.application.stock.service.command

import com.zunza.ecommerce.domain.stock.dto.RegisterStockSpec

data class RegisterStockCommand(
    val productOptionId: Long,
    val quantity: Int,
) {
    fun toSpec() = RegisterStockSpec(productOptionId, quantity)
}
