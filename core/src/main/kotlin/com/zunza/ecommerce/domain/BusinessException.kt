package com.zunza.ecommerce.domain

abstract class BusinessException(
    val errorCode: ErrorCode,
    message: String
) : RuntimeException(message)