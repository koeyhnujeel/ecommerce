package com.zunza.ecommerce.domain.product

import com.zunza.ecommerce.domain.BusinessException
import com.zunza.ecommerce.domain.ErrorCode

class InvalidImageRoleException(
    message: String = "잘못된 ImageRole 입니다. "
) : BusinessException(ErrorCode.INVALID_ROLE, message)