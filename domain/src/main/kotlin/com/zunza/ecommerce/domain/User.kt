package com.zunza.ecommerce.domain

import com.zunza.ecommerce.domain.enums.UserType
import java.time.LocalDateTime

data class User(
    val id: Long,
    val email: String,
    val password: String,
    val userType: UserType,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)
