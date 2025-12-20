package com.zunza.ecommerce.domain.account

class InvalidCredentialsException(
    message: String = "이메일 또는 비밀번호를 확인해 주세요."
) : RuntimeException(message)