package com.zunza.ecommerce.domain.brand

import com.zunza.ecommerce.domain.AbstractEntity
import jakarta.persistence.Entity

@Entity
class Brand(
    val partnerId: Long,
    val brandInfo: BrandInfo,
) : AbstractEntity<Brand>() {
    companion object {
        fun register(
            partnerId: Long,
            brandInfo: BrandInfo,
        ) = Brand(partnerId, brandInfo)
    }
}