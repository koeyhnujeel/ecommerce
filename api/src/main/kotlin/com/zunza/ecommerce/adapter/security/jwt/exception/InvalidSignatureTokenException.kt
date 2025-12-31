package com.zunza.ecommerce.adapter.security.jwt.exception

class InvalidSignatureTokenException(
    message: String = "토큰 서명이 올바르지 않습니다."
) : CustomTokenException(message)