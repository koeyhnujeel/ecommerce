package com.zunza.ecommerce.support.exception

class BusinessException(
    val errorCode: ErrorCode,
    message: String,
) : RuntimeException(message)
