package com.zunza.apis.support.exception

class BusinessException(
    val errorCode: ErrorCode,
    message: String,
) : RuntimeException(message)
