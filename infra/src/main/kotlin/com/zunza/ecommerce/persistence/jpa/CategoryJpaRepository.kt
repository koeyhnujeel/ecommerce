package com.zunza.ecommerce.persistence.jpa

import com.zunza.ecommerce.persistence.entity.CategoryEntity
import org.springframework.data.jpa.repository.JpaRepository

interface CategoryJpaRepository : JpaRepository<CategoryEntity, Long> {
}
