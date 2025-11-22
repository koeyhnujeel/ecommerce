package com.zunza.ecommerce.persistence.impl

import com.zunza.ecommerce.repository.TokenBlacklistRepository
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Repository
import java.util.concurrent.TimeUnit

@Repository
class AccessTokenBlacklistRepositoryImpl(
    private val stringRedisTemplate: StringRedisTemplate
) : TokenBlacklistRepository {
    companion object {
        private const val BLACKLIST_KEY_PREFIX = "BLACKLIST:"
    }

    override fun add(jti: String, token: String, remainingTime: Long) {
        stringRedisTemplate.opsForValue().set(
            BLACKLIST_KEY_PREFIX + jti,
            token,
            remainingTime,
            TimeUnit.MILLISECONDS
        )
    }

    override fun existsByJti(jti: String): Boolean {
        return stringRedisTemplate.hasKey(BLACKLIST_KEY_PREFIX + jti)
    }
}
