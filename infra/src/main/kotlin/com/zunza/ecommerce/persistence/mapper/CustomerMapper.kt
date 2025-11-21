package com.zunza.ecommerce.persistence.mapper

import com.zunza.ecommerce.domain.Customer
import com.zunza.ecommerce.persistence.entity.CustomerProfileEntity

fun CustomerProfileEntity.toDomain(): Customer {
    return Customer(
        id = this.id,
        email = this.userEntity.email,
        password = this.userEntity.password,
        name = this.name,
        nickname = this.nickname,
        phone = this.phone,
        point = this.point,
        userType = this.userEntity.userType,
        createdAt = this.userEntity.createdAt,
        updatedAt = this.userEntity.updatedAt
    )
}
