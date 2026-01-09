package com.zunza.ecommerce.domain.stock

import com.zunza.ecommerce.domain.AbstractEntity
import com.zunza.ecommerce.domain.stock.dto.RegisterStockSpec
import jakarta.persistence.Entity

@Entity
class Stock(
    val productOptionId: Long,
    val quantity: Int
) : AbstractEntity() {
    companion object {
        fun register(spec: RegisterStockSpec): Stock {
            require(spec.productOptionId > 0) { "상품 옵션 ID는 1 이상이어야 합니다." }
            require(spec.quantity > 0) { "재고 수량은 1개 이상이어야 합니다." }

            return Stock(
                productOptionId = spec.productOptionId,
                quantity = spec.quantity
            )
        }
    }
}