package com.zunza.domain.entity

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
@Table(name = "product_options")
class ProductOption(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    @Column(nullable = false)
    val name: String = "",
    @Column(nullable = false)
    val size: String = "",
    @Column(nullable = false)
    val color: String = "",
    @Column(nullable = false)
    val stockQuantity: Int = 0,
    @Column(nullable = false)
    val additionalPrice: BigDecimal = BigDecimal.ZERO,
    @Column(nullable = false)
    val displayOrder: Int = 1,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    val product: Product,
) : BaseTimeEntity()
