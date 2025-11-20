package com.zunza.ecommerce.support.exception

enum class ErrorCode(
    val status: Int,
    val defaultMessage: String,
) {
    EMAIL_ALREADY_EXISTS(409, "이미 사용 중인 이메일입니다."),
    PHONE_ALREADY_EXISTS(409, "이미 사용 중인 번호입니다."),
    CUSTOMER_NOT_FOUND(404, "고객 정보를 찾을 수 없습니다."),
    ;

    fun exception() = BusinessException(this, this.defaultMessage)

    fun exception(message: String) = BusinessException(this, message)
}
