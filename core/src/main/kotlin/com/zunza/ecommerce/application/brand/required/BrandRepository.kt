package com.zunza.ecommerce.application.brand.required

import com.zunza.ecommerce.domain.brand.Brand

interface BrandRepository {
    fun save(brand: Brand): Brand
}