package com.zunza.ecommerce.application.product.required

import com.zunza.ecommerce.domain.prodcut.Product

interface ProductRepository {
    fun save(product: Product): Product
}