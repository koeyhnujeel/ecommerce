package com.zunza.ecommerce.adapter.persistence.brand

import com.zunza.ecommerce.domain.brand.Brand
import org.springframework.data.jpa.repository.JpaRepository

interface BrandJpaRepository : JpaRepository<Brand, Long> {
}