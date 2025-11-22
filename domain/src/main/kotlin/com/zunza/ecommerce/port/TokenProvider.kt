package com.zunza.ecommerce.port

import com.zunza.ecommerce.domain.enums.UserType

interface TokenProvider {
    fun generateAccessToken(userId: Long, userType: UserType): String

    fun generateRefreshToken(userId: Long): String

    fun validateToken(token: String): Boolean

    fun getUserId(token: String): Long

    fun getUserRole(token: String): String

    fun getJti(token: String): String

    fun getRemainingTime(token: String): Long
}
