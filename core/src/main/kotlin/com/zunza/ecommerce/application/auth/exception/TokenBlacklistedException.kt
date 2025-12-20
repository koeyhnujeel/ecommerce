package com.zunza.ecommerce.application.auth.exception

class TokenBlacklistedException(
    message: String = "블랙리스트에 등록된 토큰입니다."
) : CustomTokenException(message) {
}