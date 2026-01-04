package com.zunza.ecommerce.domain.customer

import com.zunza.ecommerce.domain.BusinessException
import com.zunza.ecommerce.domain.ErrorCode

class ShippingAddressNotFoundException(
    message: String = "등록되지 않은 주소입니다."
) : BusinessException(ErrorCode.NOT_FOUND, message)