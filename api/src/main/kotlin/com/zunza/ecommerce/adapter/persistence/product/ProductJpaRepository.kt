package com.zunza.ecommerce.adapter.persistence.product

import com.zunza.ecommerce.domain.prodcut.Product
import org.springframework.data.jpa.repository.JpaRepository

interface ProductJpaRepository : JpaRepository<Product, Long> {
}