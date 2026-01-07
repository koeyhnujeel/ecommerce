package com.zunza.ecommerce.application.product.required

import com.zunza.ecommerce.domain.product.Product

interface ProductRepository {
    fun save(product: Product): Product
}