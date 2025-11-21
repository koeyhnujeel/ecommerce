package com.zunza.ecommerce.domain

import com.zunza.ecommerce.domain.enums.UserType
import java.time.LocalDateTime

data class Customer(
    val id: Long = 0,
    val email: String,
    val password: String,
    val name: String,
    val nickname: String,
    val phone: String,
    var point: Long,
    val userType: UserType,
    val createdAt: LocalDateTime? = null,
    val updatedAt: LocalDateTime? = null,
) {
    companion object {
        fun of(
            email: String,
            password: String,
            name: String,
            nickname: String,
            phone: String,
        ): Customer {
            return Customer(
                email = email,
                password = password,
                name = name,
                nickname = nickname,
                phone = phone,
                point = 0L,
                userType = UserType.CUSTOMER
            )
        }
    }
}
