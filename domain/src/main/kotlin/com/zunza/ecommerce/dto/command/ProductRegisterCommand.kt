package com.zunza.ecommerce.dto.command

import java.math.BigDecimal

data class ProductRegisterCommand(
    val brandId: Long,
    val categoryId: Long,
    val name: String,
    val description: String,
    val price: BigDecimal,
    val options: List<ProductOptionCommand>,
    val mainImage: UploadFile,
    val detailImages: List<UploadFile>
)

data class ProductOptionCommand(
    val size: String,
    val color: String,
    val additionalPrice: BigDecimal,
    val stockQuantity: Int,
    val displayOrder: Int
)
