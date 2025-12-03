package com.zunza.ecommerce.persistence.mapper

import com.zunza.ecommerce.domain.Customer
import com.zunza.ecommerce.domain.User
import com.zunza.ecommerce.persistence.entity.CustomerProfileEntity

fun CustomerProfileEntity.toDomain(): Customer {
    val user = User(
        id = this.userEntity.id,
        email = this.userEntity.email,
        password = this.userEntity.password,
        userType = this.userEntity.userType,
        createdAt = this.userEntity.createdAt,
        updatedAt = this.userEntity.updatedAt
    )

    return Customer(
        id = this.id,
        name = this.name,
        nickname = this.nickname,
        phone = this.phone,
        point = this.point,
        user = user
    )
}
