package com.zunza.ecommerce.persistence.impl

import com.zunza.ecommerce.repository.RefreshTokenRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Repository
import java.util.concurrent.TimeUnit

@Repository
class RefreshTokenRepositoryImpl(
    @Value("\${jwt.refresh-token-expire-time}")
    private val ttl: Long,
    private val stringRedisTemplate: StringRedisTemplate
) : RefreshTokenRepository {
    companion object {
        private const val REFRESH_TOKEN_KEY_PREFIX = "RT:"
    }

    override fun save(userId: Long, refreshToken: String) {
        stringRedisTemplate.opsForValue().set(
            REFRESH_TOKEN_KEY_PREFIX + userId,
            refreshToken,
            ttl,
            TimeUnit.MILLISECONDS
            )
    }

    override fun findByUserId(userId: Long): String? {
        return stringRedisTemplate.opsForValue().get(REFRESH_TOKEN_KEY_PREFIX + userId)
    }

    override fun deleteById(userId: Long): Boolean {
        return stringRedisTemplate.delete(REFRESH_TOKEN_KEY_PREFIX + userId)
    }
}
