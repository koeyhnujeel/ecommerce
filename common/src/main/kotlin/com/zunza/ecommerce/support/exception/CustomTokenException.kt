package com.zunza.ecommerce.support.exception

class CustomTokenException(
    override val message: String?,
) : RuntimeException(message)
