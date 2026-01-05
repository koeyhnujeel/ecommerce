package com.zunza.ecommerce.application.brand.provided

import com.zunza.ecommerce.domain.brand.BrandInfo

interface RegisterBrandUseCase {
    fun registerBrand(partnerId: Long, brandInfo: BrandInfo)
}