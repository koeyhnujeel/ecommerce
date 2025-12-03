package com.zunza.ecommerce.persistence.impl

import com.zunza.ecommerce.domain.Product
import com.zunza.ecommerce.persistence.entity.ProductEntity
import com.zunza.ecommerce.persistence.entity.ProductImageEntity
import com.zunza.ecommerce.persistence.entity.ProductOptionEntity
import com.zunza.ecommerce.persistence.jpa.BrandJpaRepository
import com.zunza.ecommerce.persistence.jpa.CategoryJpaRepository
import com.zunza.ecommerce.persistence.jpa.ProductJpaRepository
import com.zunza.ecommerce.persistence.mapper.toDomain
import com.zunza.ecommerce.repository.ProductRepository
import com.zunza.ecommerce.support.exception.ErrorCode
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository

@Repository
class ProductRepositoryImpl(
    private val productJpaRepository: ProductJpaRepository,
    private val brandJpaRepository: BrandJpaRepository,
    private val categoryJpaRepository: CategoryJpaRepository
) : ProductRepository {

    override fun save(product: Product): Product {
        val brand = brandJpaRepository.findByIdOrNull(product.brandId)
            ?: throw ErrorCode.BRAND_NOT_FOUND.exception()

        val categoryEntities = product.categoryIds.map { categoryId ->
            categoryJpaRepository.findByIdOrNull(categoryId)
                ?: throw ErrorCode.CATEGORY_NOT_FOUND.exception()
        }

        val productEntity = ProductEntity(
            name = product.name,
            description = product.description,
            price = product.price,
            status = product.status,
            brand = brand
        )

        product.options.map { option ->
            productEntity.addOption(ProductOptionEntity(
                    size = option.size,
                    color = option.color,
                    additionalPrice = option.additionalPrice,
                    stockQuantity = option.stockQuantity,
                    displayOrder = option.displayOrder,
                    product = productEntity
                )
            )
        }

        product.images.map { image ->
            productEntity.addImage(ProductImageEntity(
                    imageUrl = image.imageUrl,
                    imageType = image.imageType,
                    displayOrder = image.displayOrder,
                    product = productEntity
                )
            )
        }

        categoryEntities.forEach { categoryEntity ->
                productEntity.addCategory(categoryEntity)
        }

        val saveProductEntity = productJpaRepository.save(productEntity)

        return saveProductEntity.toDomain()
    }
}
