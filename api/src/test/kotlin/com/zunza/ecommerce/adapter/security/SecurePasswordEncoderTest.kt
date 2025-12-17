package com.zunza.ecommerce.adapter.security

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class SecurePasswordEncoderTest {
    @Test
    fun securePasswordEncoder() {
        val securePasswordEncoder = SecurePasswordEncoder()

        val passwordHash = securePasswordEncoder.encode("verysecret")

        securePasswordEncoder.matches("verysecret", passwordHash) shouldBe true
        securePasswordEncoder.matches("invalid password", passwordHash) shouldBe false
    }
}