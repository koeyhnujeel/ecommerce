package com.zunza.ecommerce.support.exception

enum class ErrorCode(
    val status: Int,
    val defaultMessage: String,
) {
    ;

    fun exception() = BusinessException(this, this.defaultMessage)

    fun exception(message: String) = BusinessException(this, message)
}
