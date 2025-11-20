package com.zunza.ecommerce.persistence.mapper

import com.zunza.ecommerce.domain.Customer
import com.zunza.ecommerce.persistence.entity.CustomerEntity

fun Customer.toEntity(): CustomerEntity {
    return CustomerEntity(
        email = this.email,
        password = this.password,
        name = this.name,
        nickname = this.nickname,
        phone = this.phone,
        point = this.point,
        userType = this.userType
    )
}

fun CustomerEntity.toDomain(): Customer {
    return Customer(
        id = this.id,
        email = this.email,
        password = this.password,
        name = this.name,
        nickname = this.nickname,
        phone = this.phone,
        point = this.point,
        userType = this.userType,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )
}
