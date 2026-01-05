package com.zunza.ecommerce.application.brand.service

import com.zunza.ecommerce.application.brand.provided.RegisterBrandUseCase
import com.zunza.ecommerce.application.brand.required.BrandRepository
import com.zunza.ecommerce.domain.brand.Brand
import com.zunza.ecommerce.domain.brand.BrandInfo
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class BrandCommandService(
    private val brandRepository: BrandRepository
) : RegisterBrandUseCase {
    override fun registerBrand(partnerId: Long, brandInfo: BrandInfo) {
        val brand = Brand.register(
            partnerId = partnerId,
            brandInfo = brandInfo
        )

        brandRepository.save(brand)
    }
}