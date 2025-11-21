package com.zunza.ecommerce.port

import com.zunza.ecommerce.domain.enums.UserType

interface TokenProvider {
    fun generateAccessToken(customerId: Long, userType: UserType): String

    fun generateRefreshToken(customerId: Long): String

    fun validateToken(token: String): Boolean

    fun getCustomerId(token: String): Long

    fun getUserRole(token: String): String
}
