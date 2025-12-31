package com.zunza.ecommerce.domain.product

import com.zunza.ecommerce.domain.AbstractEntity
import jakarta.persistence.Entity

@Entity
class ProductImage private constructor(
    val url: String,
    val type: ImageType,
    val displayOrder: Int
) : AbstractEntity() {
    companion object {
        fun create(
            url: String,
            type: ImageType,
            displayOrder: Int
        ): ProductImage {
            require(url.isNotBlank()) { "상품 이미지 경로는 필수입니다." }
            require(displayOrder > 0) { "상품 이미지 표시 순서는 0 이하일 수 없습니다." }

            return ProductImage(
                url = url,
                type = type,
                displayOrder = displayOrder
            )
        }
    }
}