package com.zunza.ecommerce.domain.customer

class CustomerNotFoundException(
    message: String = "존재하지 않는 회원입니다."
) : RuntimeException(message)