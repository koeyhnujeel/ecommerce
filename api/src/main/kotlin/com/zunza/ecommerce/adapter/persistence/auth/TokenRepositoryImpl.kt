package com.zunza.ecommerce.adapter.persistence.auth

import com.zunza.ecommerce.adapter.security.jwt.JwtProperties
import com.zunza.ecommerce.application.auth.required.TokenRepository
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Repository
import java.util.concurrent.TimeUnit

@Repository
class TokenRepositoryImpl(
    private val jwtProperties: JwtProperties,
    private val stringRedisTemplate: StringRedisTemplate,
) : TokenRepository {
    companion object {
        private const val REFRESH_TOKEN_KEY_PREFIX = "RT:"
    }

    override fun save(accountId:Long, token: String) {
        stringRedisTemplate
            .opsForValue()
            .set(getKey(accountId), token, jwtProperties.refreshTokenTtl, TimeUnit.MILLISECONDS)
    }

    override fun findById(accountId: Long): String? {
        return stringRedisTemplate
            .opsForValue()
            .get(getKey(accountId))
    }

    private fun getKey(accountId:Long) = "$REFRESH_TOKEN_KEY_PREFIX$accountId"
}