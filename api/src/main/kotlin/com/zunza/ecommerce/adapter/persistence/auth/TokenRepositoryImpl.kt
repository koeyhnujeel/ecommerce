package com.zunza.ecommerce.adapter.persistence.auth

import com.zunza.ecommerce.adapter.security.jwt.JwtProperties
import com.zunza.ecommerce.application.account.required.TokenRepository
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
        private const val BLACKLIST_KEY_PREFIX = "BLACKLIST:"
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

    override fun addBlacklist(token: String, remainingTime: Long) {
        stringRedisTemplate
            .opsForValue()
            .set("$BLACKLIST_KEY_PREFIX$token", "", remainingTime, TimeUnit.MILLISECONDS)
    }

    override fun removeToken(accountId: Long) {
        stringRedisTemplate.delete(getKey(accountId))
    }

    override fun isBlacklisted(token: String): Boolean {
        return stringRedisTemplate.hasKey("$BLACKLIST_KEY_PREFIX$token") == true
    }

    private fun getKey(accountId:Long) = "$REFRESH_TOKEN_KEY_PREFIX$accountId"
}