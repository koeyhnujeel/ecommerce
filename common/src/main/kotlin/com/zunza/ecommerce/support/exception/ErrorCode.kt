package com.zunza.ecommerce.support.exception

enum class ErrorCode(
    val status: Int,
    val defaultMessage: String,
) {
    EMAIL_ALREADY_EXISTS(409, "이미 사용 중인 이메일입니다."),
    PHONE_ALREADY_EXISTS(409, "이미 사용 중인 번호입니다."),
    CUSTOMER_NOT_FOUND(404, "고객 정보를 찾을 수 없습니다."),
    INVALID_CREDENTIALS(401, "이메일 또는 비밀번호를 확인해 주세요."),
    MISSING_TOKEN(401, "인증 토큰이 필요합니다."),
    REFRESH_TOKEN_NOT_FOUND(404, "잘못된 요청입니다. 다시 로그인해 주세요."),
    INVALID_REFRESH_TOKEN(401, "잘못된 요청입니다. 다시 로그인해 주세요."),
    INVALID_USER_ROLE(401, "유효하지 않은 사용자 권한입니다."),
    ;

    fun exception() = BusinessException(this, this.defaultMessage)

    fun exception(message: String) = BusinessException(this, message)
}
