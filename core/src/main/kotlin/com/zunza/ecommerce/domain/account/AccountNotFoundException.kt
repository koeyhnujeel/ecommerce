package com.zunza.ecommerce.domain.account

import com.zunza.ecommerce.domain.BusinessException
import com.zunza.ecommerce.domain.ErrorCode

class AccountNotFoundException(
    message: String = "존재하지 않는 계정입니다."
) : BusinessException(ErrorCode.NOT_FOUND, message)