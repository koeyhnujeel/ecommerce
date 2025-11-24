package com.zunza.ecommerce.persistence.jpa

import com.zunza.ecommerce.persistence.entity.BrandEntity
import org.springframework.data.jpa.repository.JpaRepository

interface BrandJpaRepository : JpaRepository<BrandEntity, Long> {
}
