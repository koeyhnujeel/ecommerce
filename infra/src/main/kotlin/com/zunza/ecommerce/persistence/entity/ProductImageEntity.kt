package com.zunza.ecommerce.persistence.entity

import com.zunza.ecommerce.domain.enums.ImageType
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

@Entity
@Table
class ProductImageEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    @Column(nullable = false)
    val imageUrl: String,
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val imageType: ImageType,
    @Column(nullable = false)
    val displayOrder: Int,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    val product: ProductEntity,
) : BaseTimeEntity()
