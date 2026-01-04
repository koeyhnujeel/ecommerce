package com.zunza.ecommerce.application.brand.service

import com.zunza.ecommerce.application.brand.provided.RegisterBrandUseCase
import com.zunza.ecommerce.application.brand.required.BrandRepository
import com.zunza.ecommerce.application.brand.service.dto.command.RegisterBrandCommand
import com.zunza.ecommerce.domain.brand.Brand
import org.springframework.stereotype.Service

@Service
class BrandCommandService(
    private val brandRepository: BrandRepository
) : RegisterBrandUseCase {
    override fun registerBrand(command: RegisterBrandCommand) {
        val brand = Brand.register(
            partnerId = command.partnerId,
            brandInfo = command.brandInfo
        )

        brandRepository.save(brand)
    }
}