package com.zunza.ecommerce.domain.customer

import com.zunza.ecommerce.domain.BusinessException
import com.zunza.ecommerce.domain.ErrorCode

class CustomerNotFoundException(
    message: String = "존재하지 않는 회원입니다."
) : BusinessException(ErrorCode.NOT_FOUND, message)