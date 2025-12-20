package com.zunza.ecommerce.adapter.persistence.auth

import com.zunza.ecommerce.adapter.security.jwt.JwtProperties
import com.zunza.ecommerce.config.TestContainersConfiguration
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest
import org.springframework.context.annotation.Import
import org.springframework.data.redis.core.StringRedisTemplate

@DataRedisTest
@Import(TokenRepositoryImpl::class, TestContainersConfiguration::class)
@EnableConfigurationProperties(JwtProperties::class)
class TokenRepositoryImplTest(
    val tokenRepositoryImpl: TokenRepositoryImpl,
    val stringRedisTemplate: StringRedisTemplate
) {
    @Test
    fun save() {
        val accountId = 1L
        val token = "refreshToken"

        tokenRepositoryImpl.save(accountId, token)

        val result = stringRedisTemplate.opsForValue().get("RT:$accountId")

        result shouldBe token
    }

    @Test
    fun findById() {
        val accountId = 1L
        val token = "refreshToken"

        tokenRepositoryImpl.findById(2L) shouldBe null

        tokenRepositoryImpl.save(accountId, token)

        tokenRepositoryImpl.findById(accountId) shouldBe token
    }

    @Test
    fun addBlacklist() {
        val token = "blacklist"

        tokenRepositoryImpl.addBlacklist(token, 1_000L)

        stringRedisTemplate.hasKey("BLACKLIST:$token") shouldBe true
    }

    @Test
    fun remove() {
        val accountId = 100L

        tokenRepositoryImpl.save(accountId, "delete")

        tokenRepositoryImpl.removeToken(accountId)

        stringRedisTemplate.hasKey("RT:$accountId") shouldBe false
    }

    @Test
    fun isBlacklisted() {
        val token = "blacklist"

        tokenRepositoryImpl.addBlacklist(token, 10_000L)

        tokenRepositoryImpl.isBlacklisted(token) shouldBe true
    }
}