package com.zunza.ecommerce.persistence.mapper

import com.zunza.ecommerce.domain.Product
import com.zunza.ecommerce.domain.ProductImage
import com.zunza.ecommerce.domain.ProductOption
import com.zunza.ecommerce.persistence.entity.ProductEntity

fun ProductEntity.toDomain(): Product {
    val productOptions = this.options.map { option ->
        ProductOption(
            id = option.id,
            size = option.size,
            color = option.color,
            additionalPrice = option.additionalPrice,
            stockQuantity = option.stockQuantity,
            displayOrder = option.displayOrder,
            createdAt = option.createdAt,
            updatedAt = option.updatedAt
        )
    }

    val productImages = this.images.map { image ->
        ProductImage(
            id = image.id,
            imageUrl = image.imageUrl,
            imageType = image.imageType,
            displayOrder = image.displayOrder,
            createdAt = image.createdAt,
            updatedAt = image.updatedAt
        )
    }

    return Product(
        id = this.id,
        brandId = this.brand.id,
        categoryIds = this.productCategories.map { it.category.id },
        name = this.name,
        description = this.description,
        price = this.price,
        status = this.status,
        options = productOptions,
        images = productImages,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )
}
