package com.zunza.ecommerce.domain.account

class AccountNotFoundException(
    message: String = "존재하지 않는 계정입니다."
) : RuntimeException(message)