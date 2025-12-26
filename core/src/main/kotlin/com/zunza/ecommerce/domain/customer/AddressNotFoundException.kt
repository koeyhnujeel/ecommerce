package com.zunza.ecommerce.domain.customer

class AddressNotFoundException(
    message: String = "등록되지 않은 주소입니다."
) : RuntimeException(message)