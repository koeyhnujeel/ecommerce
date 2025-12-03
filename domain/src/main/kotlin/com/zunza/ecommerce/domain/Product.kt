package com.zunza.ecommerce.domain

import com.zunza.ecommerce.domain.enums.ImageType
import com.zunza.ecommerce.domain.enums.ProductStatus
import com.zunza.ecommerce.dto.command.ProductOptionCommand
import java.math.BigDecimal
import java.time.LocalDateTime

data class Product(
    val id: Long = 0,
    val brandId: Long,
    val categoryIds: List<Long>,
    val name: String,
    val description: String,
    val price: BigDecimal,
    val status: ProductStatus,
    val options: List<ProductOption>,
    val images: List<ProductImage>,
    val createdAt: LocalDateTime? = null,
    val updatedAt: LocalDateTime? = null
) {
    companion object {
        fun of(
            brandId: Long,
            categoryId: Long,
            name: String,
            description: String,
            price: BigDecimal,
            status: ProductStatus,
            options: List<ProductOption>,
            images: List<ProductImage>
            ): Product {
            return Product(
                brandId = brandId,
                categoryIds = listOf(categoryId),
                name = name,
                description = description,
                price = price,
                status = status,
                options = options,
                images = images
            )
        }
    }
}

data class ProductOption(
    val id: Long = 0,
    val size: String,
    val color: String,
    val additionalPrice: BigDecimal,
    val stockQuantity: Int,
    val displayOrder: Int,
    val createdAt: LocalDateTime? = null,
    val updatedAt: LocalDateTime? = null
) {
    companion object {
        fun from(command: ProductOptionCommand): ProductOption {
            return ProductOption(
                size = command.size,
                color = command.color,
                additionalPrice = command.additionalPrice,
                stockQuantity = command.stockQuantity,
                displayOrder = command.displayOrder
            )
        }
    }
}

data class ProductImage(
    val id: Long = 0,
    val imageUrl: String,
    val imageType: ImageType,
    val displayOrder: Int,
    val createdAt: LocalDateTime? = null,
    val updatedAt: LocalDateTime? = null
) {
    companion object {
        fun of(
            imageUrl: String,
            imageType: ImageType,
            displayOrder: Int
        ): ProductImage {
            return ProductImage(
                imageUrl = imageUrl,
                imageType = imageType,
                displayOrder = displayOrder
            )
        }
    }
}
