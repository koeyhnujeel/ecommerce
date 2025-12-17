package com.zunza.ecommerce.domain.account

interface PasswordEncoder {
    fun encode(password: String): String

    fun matches(password: String, passwordHash: String): Boolean
}