package com.zunza.ecommerce.persistence.entity

import com.zunza.ecommerce.domain.enums.ProductStatus
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.math.BigDecimal

@Entity
@Table(name = "products")
class ProductEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    @Column(nullable = false)
    val name: String,
    @Column(nullable = false, columnDefinition = "TEXT")
    val description: String,
    @Column(nullable = false)
    val price: BigDecimal,
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: ProductStatus,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id", nullable = false)
    val brand: BrandEntity,
) : BaseTimeEntity()
