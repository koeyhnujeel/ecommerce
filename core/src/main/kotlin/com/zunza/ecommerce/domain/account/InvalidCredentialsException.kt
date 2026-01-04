package com.zunza.ecommerce.domain.account

import com.zunza.ecommerce.domain.BusinessException
import com.zunza.ecommerce.domain.ErrorCode

class InvalidCredentialsException(
    message: String = "이메일 또는 비밀번호를 확인해 주세요."
) : BusinessException(ErrorCode.INVALID_CREDENTIALS, message)