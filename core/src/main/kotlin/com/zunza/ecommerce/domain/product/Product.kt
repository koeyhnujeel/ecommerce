package com.zunza.ecommerce.domain.product

import com.zunza.ecommerce.domain.AbstractEntity
import jakarta.persistence.Entity
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
class Product private constructor(
    val sellerId: Long,
    val categoryId: Long,
    val brandId: Long,
    val name: String,
    val description: String,
    val basePrice: BigDecimal,
    var approvalStatus: ApprovalStatus,
    var salesStatus: SalesStatus,
    val registeredAt: LocalDateTime,
    var updatedAt: LocalDateTime,
    val productOptions: MutableList<ProductOption> = mutableListOf(),
    val productImages: MutableList<ProductImage> = mutableListOf(),
) : AbstractEntity() {
    companion object {
        fun register(
            sellerId: Long,
            categoryId: Long,
            brandId: Long,
            name: String,
            description: String,
            basePrice: BigDecimal,
        ): Product {
            require(sellerId > 0) { "판매자 ID는 0 이하일 수 없습니다." }
            require(categoryId > 0) { "카테고리 ID는 0 이하일 수 없습니다." }
            require(brandId > 0) { "브랜드 ID는 0 이하일 수 없습니다." }
            require(name.isNotBlank()) { "상품명은 필수입니다." }
            require(description.isNotBlank()) { "상품 설명은 필수입니다." }
            require(basePrice >= 100.toBigDecimal()) { "상품 기본 가격은 100원 미만일 수 없습니다." }

            return Product(
                sellerId = sellerId,
                categoryId = categoryId,
                brandId = brandId,
                name = name,
                description = description,
                basePrice = basePrice,
                approvalStatus = ApprovalStatus.PENDING,
                salesStatus = SalesStatus.PREPARING,
                registeredAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now(),
            )
        }
    }

    fun addOption(
        color: String,
        size: String,
        additionalPrice: BigDecimal,
    ) {
        val productOption = ProductOption.create(color, size, additionalPrice)

        productOptions.add(productOption)
    }

    fun addImage(
        url: String,
        type: ImageType,
        displayOrder: Int,
    ) {
        val productImage = ProductImage.create(url, type, displayOrder)

        productImages.add(productImage)
    }

    fun approve() {
        if (this.approvalStatus == ApprovalStatus.APPROVED) return

        this.approvalStatus = ApprovalStatus.APPROVED
    }

    fun openSales() {
        if (this.salesStatus == SalesStatus.ON_SALE) return

        check(this.approvalStatus == ApprovalStatus.APPROVED) { "상품 등록 승인 상태가 아닙니다." }

        this.salesStatus = SalesStatus.ON_SALE
    }
}