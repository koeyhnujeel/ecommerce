package com.zunza.ecommerce.domain.onboarding

import com.zunza.ecommerce.domain.BusinessException
import com.zunza.ecommerce.domain.ErrorCode

class DuplicateBusinessNumberException(
    message: String = "이미 신청된 사업자등록번호입니다."
) : BusinessException(ErrorCode.DUPLICATE, message)