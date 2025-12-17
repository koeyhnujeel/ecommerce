package com.zunza.ecommerce.domain.account

class DuplicateEmailException(
    message: String = "이미 사용 중인 이메일입니다."
) : RuntimeException(message)