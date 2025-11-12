package com.zunza.common.support.exception

class CustomTokenException(
    override val message: String?,
) : RuntimeException(message)
