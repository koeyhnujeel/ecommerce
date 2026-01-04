package com.zunza.ecommerce.domain.account

import com.zunza.ecommerce.domain.BusinessException
import com.zunza.ecommerce.domain.ErrorCode

class DuplicateEmailException(
    message: String = "이미 사용 중인 이메일입니다."
) : BusinessException(ErrorCode.DUPLICATE, message)