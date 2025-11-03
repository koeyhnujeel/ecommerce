package com.zunza.apis.auth.jwt.exception

class CustomTokenException(
    override val message: String?,
) : RuntimeException(message)
