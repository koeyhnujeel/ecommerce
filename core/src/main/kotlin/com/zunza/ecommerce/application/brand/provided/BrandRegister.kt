package com.zunza.ecommerce.application.brand.provided

import com.zunza.ecommerce.domain.brand.BrandInfo

interface BrandRegister {
    fun registerBrand(partnerId: Long, brandInfo: BrandInfo)
}