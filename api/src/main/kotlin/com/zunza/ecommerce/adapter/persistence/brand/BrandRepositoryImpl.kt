package com.zunza.ecommerce.adapter.persistence.brand

import com.zunza.ecommerce.application.brand.required.BrandRepository
import com.zunza.ecommerce.domain.brand.Brand
import org.springframework.stereotype.Repository

@Repository
class BrandRepositoryImpl(
    private val brandJpaRepository: BrandJpaRepository
) : BrandRepository {
    override fun save(brand: Brand): Brand {
        return brandJpaRepository.save(brand)
    }
}