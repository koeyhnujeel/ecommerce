package com.zunza.ecommerce.adapter.security.jwt

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "jwt")
data class JwtProperties(
    val secret: String,
    val accessTokenTtl: Long,
    val refreshTokenTtl: Long
)