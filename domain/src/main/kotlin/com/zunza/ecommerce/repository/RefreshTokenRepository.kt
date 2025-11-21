package com.zunza.ecommerce.repository

interface RefreshTokenRepository {
    fun save(userId: Long, refreshToken: String)

    fun findByUserId(userId: Long): String?
}
