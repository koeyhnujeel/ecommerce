package com.zunza.ecommerce.domain.prodcut

import com.zunza.ecommerce.domain.AbstractEntity
import com.zunza.ecommerce.domain.prodcut.dto.RegisterProductSpec
import com.zunza.ecommerce.domain.shared.Money
import jakarta.persistence.Entity
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
class Product(
    val brandId: Long,
    val categoryIds: Set<Long>,
    val name: String,
    val description: String,
    val basePrice: Money,
    val status: ProductStatus,
    val productImages: MutableList<ProductImage>,
    val productOptions: MutableList<ProductOption>,
    val registeredAt: LocalDateTime,
    val updatedAt: LocalDateTime
) : AbstractEntity<Product>() {
    companion object {
        fun register(spec: RegisterProductSpec): Product {
            require(spec.brandId > 0) { "브랜드 ID는 1 이상이어야 합니다."}
            require(spec.categoryIds.isNotEmpty()) { "카테고리는 1개 이상이어야 합니다." }
            require(spec.name.isNotBlank()) { "상품명은 필수입니다." }
            require(spec.description.isNotBlank()) { "상품 소개는 필수입니다." }
            require(spec.basePrice >= BigDecimal("100")) { "상품 기본 가격은 100원 이상이어야 합니다." }
            require(spec.images.isNotEmpty()) { "상품 이미지는 1장 이상이어야 합니다." }
            require(spec.options.isNotEmpty()) { "상품 옵션 1개 이상이어야 합니다." }

            val images = spec.images.map { ProductImage.register(it) }.toMutableList()
            val options = spec.options.map { ProductOption.register(it) }.toMutableList()

            return Product(
                brandId = spec.brandId,
                categoryIds = spec.categoryIds.toSet(),
                name = spec.name,
                description = spec.description,
                basePrice = Money(spec.basePrice),
                status = ProductStatus.ON_SALE,
                productImages = images,
                productOptions = options,
                registeredAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now()
            )
        }
    }
}