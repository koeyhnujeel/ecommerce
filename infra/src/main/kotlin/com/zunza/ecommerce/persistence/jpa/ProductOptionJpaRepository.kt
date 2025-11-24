package com.zunza.ecommerce.persistence.jpa

import com.zunza.ecommerce.persistence.entity.ProductOptionEntity
import org.springframework.data.jpa.repository.JpaRepository

interface ProductOptionJpaRepository : JpaRepository<ProductOptionEntity, Long> {
}
