package com.zunza.ecommerce.application.brand.service.dto.command

import com.zunza.ecommerce.domain.brand.BrandInfo

data class RegisterBrandCommand(
    val partnerId: Long,
    val brandInfo: BrandInfo
) {
    companion object {
        fun of(partnerId: Long, brandInfo: BrandInfo) =
            RegisterBrandCommand(partnerId, brandInfo)
    }
}
