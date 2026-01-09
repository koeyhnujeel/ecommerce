package com.zunza.ecommerce.domain.brand

import com.zunza.ecommerce.domain.AbstractEntity
import jakarta.persistence.Entity

@Entity
class Brand(
    val sellerId: Long,
    val brandInfo: BrandInfo,
) : AbstractEntity() {
    companion object {
        fun register(
            sellerId: Long,
            brandInfo: BrandInfo,
        ) = Brand(sellerId, brandInfo)
    }
}