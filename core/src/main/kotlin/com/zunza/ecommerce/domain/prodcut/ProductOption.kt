package com.zunza.ecommerce.domain.prodcut

import com.zunza.ecommerce.domain.AbstractEntity
import com.zunza.ecommerce.domain.prodcut.dto.RegisterProductOptionSpec
import com.zunza.ecommerce.domain.shared.Money
import jakarta.persistence.Entity

@Entity
class ProductOption private constructor(
    val color: String,
    val size: String,
    val additionalPrice: Money,
    val status: ProductOptionStatus
) : AbstractEntity<ProductOption>() {
    companion object {
        fun register(spec: RegisterProductOptionSpec): ProductOption {
            require(spec.color.isNotBlank()) { "상품 옵션 색상은 필수입니다." }
            require(spec.size.isNotBlank()) { "상품 옵션 사이즈는 필수입니다." }

            return ProductOption(
                color = spec.color,
                size = spec.size,
                additionalPrice = Money(spec.additionalPrice),
                status = ProductOptionStatus.ON_SALE
            )
        }
    }
}