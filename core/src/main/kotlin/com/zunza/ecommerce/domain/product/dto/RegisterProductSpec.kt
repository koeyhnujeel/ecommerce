package com.zunza.ecommerce.domain.prodcut.dto

import java.math.BigDecimal

data class RegisterProductSpec(
    val brandId: Long,
    val categoryIds: List<Long>,
    val name: String,
    val description: String,
    val basePrice: BigDecimal,
    val images: List<RegisterProductImageSpec>,
    val options: List<RegisterProductOptionSpec>
)

data class RegisterProductImageSpec(
    val url: String,
    val type: String,
    val displayOrder: Int
)

data class RegisterProductOptionSpec(
    val color: String,
    val size: String,
    val additionalPrice: BigDecimal,
)