package com.zunza.ecommerce.port

interface PasswordEncoder {
    fun encode(rawPassword: String): String

    fun matches(rawPassword: String, encodedPassword: String): Boolean
}
