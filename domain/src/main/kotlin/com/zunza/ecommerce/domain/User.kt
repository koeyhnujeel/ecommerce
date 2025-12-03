package com.zunza.ecommerce.domain

import com.zunza.ecommerce.domain.enums.UserType
import java.time.LocalDateTime

data class User(
    val id: Long = 0,
    val email: String,
    val password: String,
    val userType: UserType,
    val createdAt: LocalDateTime? = null,
    val updatedAt: LocalDateTime? = null
) {
    companion object {
        fun of(email: String, password: String, userType: UserType) =
            User(email = email, password = password, userType = userType)
    }
}
