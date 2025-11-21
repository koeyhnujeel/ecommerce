package com.zunza.ecommerce.repository

import com.zunza.ecommerce.domain.Customer

interface CustomerRepository {
    fun save(customer: Customer): Customer

    fun existsByNickname(nickname: String): Boolean

    fun existsByPhone(phone: String): Boolean

    fun findByIdOrThrow(id: Long): Customer
}
