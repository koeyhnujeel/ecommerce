package com.zunza.ecommerce.persistence.jpa

import com.zunza.ecommerce.persistence.entity.ProductEntity
import org.springframework.data.jpa.repository.JpaRepository

interface ProductJpaRepository : JpaRepository<ProductEntity, Long> {
}
