package com.zunza.ecommerce.repository

import com.zunza.ecommerce.domain.Product

interface ProductRepository {
    fun save(product: Product): Product
}
