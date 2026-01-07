package com.zunza.ecommerce.domain.product

import com.zunza.ecommerce.domain.AbstractEntity
import com.zunza.ecommerce.domain.product.dto.RegisterProductImageSpec
import jakarta.persistence.Entity

@Entity
class ProductImage private constructor(
    val url: String,
    val type: ImageType,
    val displayOrder: Int
) : AbstractEntity<ProductImage>() {
    companion object {
        fun register(spec: RegisterProductImageSpec): ProductImage {
            require(spec.url.isNotBlank()) { "상품 이미지 URL은 필수입니다." }
            require(spec.type.isNotBlank()) { "상품 이미지 타입은 필수입니다." }
            require(spec.displayOrder > 0) { "상품 표시 순서는 0 이상이어야 합니다." }

            return ProductImage(
                url = spec.url,
                type = ImageType.from(spec.type),
                displayOrder = spec.displayOrder
            )
        }
    }
}