package com.zunza.ecommerce.persistence.jpa

import com.zunza.ecommerce.persistence.entity.ProductImageEntity
import org.springframework.data.jpa.repository.JpaRepository

interface ProductImageJpaRepository : JpaRepository<ProductImageEntity, Long> {
}
