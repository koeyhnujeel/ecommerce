package com.zunza.ecommerce.application.product.service.dto.command

import com.zunza.ecommerce.domain.product.dto.RegisterProductImageSpec
import com.zunza.ecommerce.domain.product.dto.RegisterProductOptionSpec
import com.zunza.ecommerce.domain.product.dto.RegisterProductSpec
import java.math.BigDecimal

data class RegisterProductCommand(
    val brandId: Long,
    val categoryIds: List<Long>,
    val name: String,
    val description: String,
    val basePrice: BigDecimal,
    val images: List<RegisterProductImageCommand>,
    val options: List<RegisterProductOptionCommand>
) {
    fun toSpec() =
        RegisterProductSpec(
            brandId = brandId,
            categoryIds = categoryIds,
            name = name,
            description = description,
            basePrice = basePrice,
            images = images.map { it.toSpec() },
            options = options.map { it.toSpec() }
        )
}

data class RegisterProductImageCommand(
    val url: String,
    val type: String,
    val displayOrder: Int
) {
    fun toSpec() =
        RegisterProductImageSpec(
            url = url,
            type = type,
            displayOrder = displayOrder,
        )
}

data class RegisterProductOptionCommand(
    val color: String,
    val size: String,
    val additionalPrice: BigDecimal,
    val quantity: Int
) {
    fun toSpec() =
        RegisterProductOptionSpec(
            color = color,
            size = size,
            additionalPrice = additionalPrice,
        )
}