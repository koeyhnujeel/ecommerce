package com.zunza.domain.entity

import com.zunza.domain.enums.ProductStatus
import jakarta.persistence.Column
import jakarta.persistence.Entity
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
class Product(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    @Column(nullable = false)
    val name: String = "",
    @Column(nullable = false)
    val description: String = "",
    @Column(nullable = false)
    val basePrice: BigDecimal = BigDecimal.ZERO,
    @Column(nullable = false)
    val status: ProductStatus = ProductStatus.ON_SALE,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id", nullable = false)
    val brand: Brand,
) : BaseTimeEntity()
