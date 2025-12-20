package com.zunza.ecommerce.adapter.security.jwt

import com.zunza.ecommerce.config.TestConfiguration
import com.zunza.ecommerce.config.TestContainersConfiguration
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(classes = [TestConfiguration::class, TestContainersConfiguration::class])
class JwtTokenProviderTest(
    val jwtTokenProvider: JwtTokenProvider
) {
    lateinit var accessToken: String

    @BeforeEach
    fun setUp() {
        accessToken = jwtTokenProvider.generateAccessToken(1L, "ROLE_CUSTOMER")
    }

    @Test
    fun generateAccessToken() {
        val accessToken = jwtTokenProvider.generateAccessToken(1L, "ROLE_CUSTOMER")

        accessToken shouldNotBe null
    }

    @Test
    fun generateRefreshToken() {
        val refreshToken = jwtTokenProvider.generateRefreshToken(1L)

        refreshToken shouldNotBe null
    }

    @Test
    fun validateToken() {
        jwtTokenProvider.validateToken(accessToken)
    }

    @Test
    fun getAccountId() {
        val accountId = jwtTokenProvider.getAccountId(accessToken)

        accountId shouldBe 1L
    }

    @Test
    fun getAccountRole() {
        val accountRole = jwtTokenProvider.getAccountRole(accessToken)

        accountRole shouldBe "ROLE_CUSTOMER"
    }

}