package com.zunza.ecommerce.repository

interface TokenBlacklistRepository {
    fun add(jti: String, token: String, remainingTime: Long)

    fun existsByJti(jti: String): Boolean
}
