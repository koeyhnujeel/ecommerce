package com.zunza.ecommerce.domain.category

import com.zunza.ecommerce.domain.AbstractEntity
import jakarta.persistence.Entity

@Entity
class Category(
    val name: String,
    val parentId: Long,
    val depth: Int,
    val displayOrder: Int
) : AbstractEntity<Category>()