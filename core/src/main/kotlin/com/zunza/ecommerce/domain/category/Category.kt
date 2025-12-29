package com.zunza.ecommerce.domain.category

import com.zunza.ecommerce.domain.AbstractEntity
import jakarta.persistence.Entity

@Entity
class Category private constructor(
    val name: String,
    val parentId: Long?,
    val depth: Int,
    val displayOrder: Int,
    val isActive: Boolean
) : AbstractEntity() {
    init {
        if (isRoot() && parentId != null) {
            throw IllegalArgumentException("최상위 카테고리는 부모 카테고리를 가질 수 없습니다.")
        }
    }

    companion object {
        fun create(
            name: String,
            parentId: Long?,
            depth: Int,
            displayOrder: Int,
            isActive: Boolean = true
        ): Category {
            require(name.isNotBlank()) { "카테고리 이름은 필수입니다." }
            require(depth <= 3) { "카테고리 계층은 최대 3단계까지 가능합니다." }
            require(displayOrder >= 1) { "카테고리 표시 순서는 1 이상이어야 합니다." }

            return Category(name, parentId, depth, displayOrder, isActive)
        }
    }

    private fun isRoot(): Boolean {
        return depth == 1
    }
}