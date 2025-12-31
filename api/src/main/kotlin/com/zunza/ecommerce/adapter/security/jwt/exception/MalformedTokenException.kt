package com.zunza.ecommerce.adapter.security.jwt.exception

class MalformedTokenException(
    message: String = "잘못된 형식의 토큰입니다."
) : CustomTokenException(message)