package com.zunza.ecommerce.domain.onboarding

import com.zunza.ecommerce.domain.BusinessException
import com.zunza.ecommerce.domain.ErrorCode

class PartnerApplicationNotFoundException(
    message: String = "존재하지 않는 신청서입니다."
) : BusinessException(ErrorCode.NOT_FOUND, message)