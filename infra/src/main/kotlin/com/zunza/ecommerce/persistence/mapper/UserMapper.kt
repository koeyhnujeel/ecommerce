package com.zunza.ecommerce.persistence.mapper

import com.zunza.ecommerce.domain.User
import com.zunza.ecommerce.persistence.entity.UserEntity

fun UserEntity.toDomain(): User {
    return User(
        id = this.id,
        email = this.email,
        password = this.password,
        userType = this.userType,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )
}
