package com.zunza.ecommerce.domain.customer

class ShippingAddressNotFoundException(
    message: String = "등록되지 않은 주소입니다."
) : RuntimeException(message)