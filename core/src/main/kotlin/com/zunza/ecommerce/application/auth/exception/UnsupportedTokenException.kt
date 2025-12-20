package com.zunza.ecommerce.application.auth.exception

class UnsupportedTokenException(
    message: String = "지원하지 않는 토큰입니다."
) : CustomTokenException(message)