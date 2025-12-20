package com.zunza.ecommerce.application.auth.exception

class MalformedTokenException(
    message: String = "잘못된 형식의 토큰입니다."
) : CustomTokenException(message)