package com.zunza.ecommerce.persistence.jpa

import com.zunza.ecommerce.persistence.entity.ProductCategoryEntity
import org.springframework.data.jpa.repository.JpaRepository

interface ProductCategoryJpaRepository : JpaRepository<ProductCategoryEntity, Long> {
}
