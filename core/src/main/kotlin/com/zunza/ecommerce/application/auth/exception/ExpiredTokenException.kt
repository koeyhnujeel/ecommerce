package com.zunza.ecommerce.application.auth.exception

class ExpiredTokenException(
    message: String = "토큰이 만료되었습니다."
) : CustomTokenException(message)