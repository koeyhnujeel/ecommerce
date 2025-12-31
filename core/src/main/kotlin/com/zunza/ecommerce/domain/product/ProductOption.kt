package com.zunza.ecommerce.domain.product

import com.zunza.ecommerce.domain.AbstractEntity
import jakarta.persistence.Entity
import java.math.BigDecimal

@Entity
class ProductOption private constructor(
    val color: String,
    val size: String,
    val additionalPrice: BigDecimal,
    val availability: Availability
) : AbstractEntity() {
    companion object {
        fun create(
            color: String,
            size: String,
            additionalPrice: BigDecimal
        ): ProductOption {
            require(color.isNotBlank()) { "상품 색상은 필수입니다." }
            require(size.isNotBlank()) { "상품 사이즈(용량)는 필수입니다." }

            return ProductOption(
                color = color,
                size = size,
                additionalPrice = additionalPrice,
                availability = Availability.IN_STOCK
            )
        }
    }
}