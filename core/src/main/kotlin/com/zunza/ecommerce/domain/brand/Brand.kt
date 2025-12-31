package com.zunza.ecommerce.domain.brand

import com.zunza.ecommerce.domain.AbstractEntity
import jakarta.persistence.Entity

@Entity
class Brand(
    val sellerId: Long,
    val name: String,
    val logoUrl: String,
    val description: String,
    val isActive: Boolean
) : AbstractEntity()