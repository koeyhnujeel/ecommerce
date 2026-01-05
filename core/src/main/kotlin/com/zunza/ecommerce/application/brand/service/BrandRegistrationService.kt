package com.zunza.ecommerce.application.brand.service

import com.zunza.ecommerce.application.brand.provided.BrandRegister
import com.zunza.ecommerce.application.brand.required.BrandRepository
import com.zunza.ecommerce.domain.brand.Brand
import com.zunza.ecommerce.domain.brand.BrandInfo
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class BrandRegistrationService(
    private val brandRepository: BrandRepository
) : BrandRegister {
    override fun registerBrand(partnerId: Long, brandInfo: BrandInfo) {
        val brand = Brand.register(
            sellerId = partnerId,
            brandInfo = brandInfo
        )

        brandRepository.save(brand)
    }
}