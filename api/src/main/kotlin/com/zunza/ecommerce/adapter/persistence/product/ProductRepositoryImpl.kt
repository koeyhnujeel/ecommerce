package com.zunza.ecommerce.adapter.persistence.product

import com.zunza.ecommerce.application.product.required.ProductRepository
import com.zunza.ecommerce.domain.prodcut.Product
import org.springframework.stereotype.Repository

@Repository
class ProductRepositoryImpl(
    private val productJpaRepository: ProductJpaRepository
) : ProductRepository {
    override fun save(product: Product): Product {
        return productJpaRepository.save(product)
    }
}