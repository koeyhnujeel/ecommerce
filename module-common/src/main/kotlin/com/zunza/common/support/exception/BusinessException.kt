package com.zunza.common.support.exception

class BusinessException(
    val errorCode: ErrorCode,
    message: String,
) : RuntimeException(message)
